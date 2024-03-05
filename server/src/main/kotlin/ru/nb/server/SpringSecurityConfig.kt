package ru.nb.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SpringSecurityConfig {

	@Bean
	fun filterChain(http: HttpSecurity): SecurityFilterChain {

		// конвертер для настройки spring security
//		val jwtAuthenticationConverter = JwtAuthenticationConverter()
		// подключаем конвертер ролей
//		jwtAuthenticationConverter.apply {
//			setJwtGrantedAuthoritiesConverter(KCRoleConverter())
//			setPrincipalClaimName("name")
//		}

		http
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.csrf { csrf -> csrf.disable() }
			.authorizeHttpRequests { auth ->
//				auth.anyRequest().authenticated()
//				auth.anyRequest().permitAll()
				auth.anyRequest().hasRole("SYSTEM")
//				auth.anyRequest().hasAnyRole("MANAGER", "ADMIN")
//				auth.anyRequest().hasRole("manage-account")
			}
			.oauth2ResourceServer { config ->
				/*	config.jwt {
						it.jwtAuthenticationConverter(jwtAuthenticationConverter)
					}*/
//				config.jwt(Customizer.withDefaults())

				config.jwt { jwt ->
					val jwtAuthenticationConverter = JwtAuthenticationConverter().apply {
						setPrincipalClaimName("preferred_username")
//						setPrincipalClaimName("name")
					}
					jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)

					jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { token ->
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

				}
			}
		return http.build()
	}

//	@Bean
//	fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
//		return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
//	}

}