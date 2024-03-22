package ru.nb.serverflux

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SpringSecurityConfig {

	@Bean
	fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
		http
			.csrf { csrf -> csrf.disable() }
			.authorizeExchange { auth ->
				auth.anyExchange().hasRole("SYSTEM")
			}
			.oauth2ResourceServer { config ->
				config.jwt(Customizer.withDefaults())
			}
		return http.build()
	}

	@Bean
	fun reactiveJwtAuthenticationConverter(): ReactiveJwtAuthenticationConverter {
		return ReactiveJwtAuthenticationConverter().apply {
			setPrincipalClaimName("preferred_username")
//						setPrincipalClaimName("name")
			setJwtGrantedAuthoritiesConverter(
				ReactiveJwtGrantedAuthoritiesConverterAdapter { token ->
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
			)
		}
	}

}