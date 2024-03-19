package ru.nb.serverflux

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SpringSecurityConfig {

	@Bean
	fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {

		// конвертер для настройки spring security
//		val jwtAuthenticationConverter = JwtAuthenticationConverter()
		// подключаем конвертер ролей
//		jwtAuthenticationConverter.apply {
//			setJwtGrantedAuthoritiesConverter(KCRoleConverter())
//			setPrincipalClaimName("name")
//		}

		http
//			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.csrf { csrf -> csrf.disable() }
			.authorizeExchange { auth ->
//				auth.anyExchange().authenticated()
//				auth.anyExchange().permitAll()
				auth.anyExchange().hasRole("SYSTEM")
//				auth.anyExchange().hasAnyRole("MANAGER", "ADMIN")
//				auth.anyExchange().hasRole("manage-account")
			}
			.oauth2ResourceServer { config ->
				/*	config.jwt {
						it.jwtAuthenticationConverter(jwtAuthenticationConverter)
					}*/
//				config.jwt(Customizer.withDefaults())

				config.jwt { jwt ->
					val jwtAuthenticationConverter = ReactiveJwtAuthenticationConverter().apply {
						setPrincipalClaimName("preferred_username")
//						setPrincipalClaimName("name")
					}
					jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)

					jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
						ReactiveJwtGrantedAuthoritiesConverterAdapter { token ->
							val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
							val grantedScopes = jwtGrantedAuthoritiesConverter.convert(token)
							println("Granted scopes: $grantedScopes")

							val groupsJwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
								setAuthoritiesClaimName("groups")
								setAuthorityPrefix("")
							}
							val grantedGroups = groupsJwtGrantedAuthoritiesConverter.convert(token)?.filter {
								it.authority.startsWith("ROLE_")
							}
							println("Granted groups: $grantedGroups")

							grantedGroups?.let {
								grantedScopes?.addAll(it)
							}

							println("All: $grantedScopes")
							grantedScopes
						}
					)
				}
			}
		return http.build()
	}

//	@Bean
//	fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
//		return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
//	}

}