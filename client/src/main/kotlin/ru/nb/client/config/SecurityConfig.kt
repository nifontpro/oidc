package ru.nb.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain
import java.util.stream.Stream

@Configuration
class SecurityConfig {

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		return http
			.authorizeHttpRequests { customizer ->
				customizer.requestMatchers("/test/**").permitAll()
				customizer.anyRequest().hasAnyRole("MANAGER", "ADMIN")
//				customizer.anyRequest().authenticated()
			}
			.oauth2Client(Customizer.withDefaults())
			.oauth2Login(Customizer.withDefaults())
			.build()
	}

	@Bean
	fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
		val oidcUserService = OidcUserService()
		return OAuth2UserService { userRequest: OidcUserRequest? ->
			val oidcUser = oidcUserService.loadUser(userRequest)
			val grantedAuthorities = Stream.concat(oidcUser.authorities.stream(),
				oidcUser.getClaimAsStringList("groups").stream()
//					.filter { authority -> authority.startsWith("ROLE_") }
					.map { role -> SimpleGrantedAuthority(role) })
				.toList()
			DefaultOidcUser(
				grantedAuthorities, oidcUser.idToken, oidcUser.userInfo,
				"preferred_username"
			)
		}
	}

	@Bean
	fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
		return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
	}
}
