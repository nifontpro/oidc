package ru.nb.serverflux

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SpringSecurityConfig {

	@Bean
	fun filterChain(http: HttpSecurity): SecurityFilterChain {
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
				config.jwt(Customizer.withDefaults())

				/*config.jwt { jwt ->
					val jwtAuthenticationConverter = JwtAuthenticationConverter().apply {
						setPrincipalClaimName("preferred_username")
//						setPrincipalClaimName("name")
					}
					jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)

					jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { token ->
						val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
						// Права по умолчанию
						val grantedScopes = jwtGrantedAuthoritiesConverter.convert(token) ?: emptyList()
						println("Granted scopes: $grantedScopes")

						// Если берем роли из realm_access/roles
						val realmAccess = token.getClaimAsMap("realm_access")
						val roles = realmAccess["roles"] as? List<*>
						val grantedRoles = roles?.let { roleList ->
							roleList
								.mapNotNull { it as? String }
								.filter { it.startsWith("ROLE_") }
								.map { SimpleGrantedAuthority(it) }
						} ?: emptyList()

						println("Granted roles: $grantedRoles")
						val allGranted = grantedScopes + grantedRoles

						*//*val groupsJwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
							setAuthoritiesClaimName("groups")
							setAuthorityPrefix("")
						}
						val grantedGroups = groupsJwtGrantedAuthoritiesConverter.convert(token)?.filter {
							it.authority.startsWith("ROLE_")
						}
						println("Granted groups: $grantedGroups")

						grantedGroups?.let {
							grantedScopes?.addAll(it)
						}*//*

						println("All: $allGranted")
						allGranted
					}*/

//				}
			}
		return http.build()
	}

	@Bean
	fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
		val jwtAuthenticationConverter = JwtAuthenticationConverter().apply {
			setPrincipalClaimName("preferred_username")
//						setPrincipalClaimName("name")
		}
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { token ->
			val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
			// Права по умолчанию
			val grantedScopes = jwtGrantedAuthoritiesConverter.convert(token) ?: emptyList()
			println("Granted scopes: $grantedScopes")

			// Если берем роли из realm_access/roles
			val realmAccess = token.getClaimAsMap("realm_access")
			val roles = realmAccess["roles"] as? List<*>
			val grantedRoles = roles?.let { roleList ->
				roleList
					.mapNotNull { it as? String }
					.filter { it.startsWith("ROLE_") }
					.map { SimpleGrantedAuthority(it) }
			} ?: emptyList()

			println("Granted roles: $grantedRoles")
			val allGranted = grantedScopes + grantedRoles
			println("All: $allGranted")
			allGranted
		}
		return jwtAuthenticationConverter
	}

//	@Bean
//	fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
//		return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
//	}

}