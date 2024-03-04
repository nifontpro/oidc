package ru.nb.client.controller

import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestClient
import ru.nb.oidc.base.Greetings

@Controller
class ServiceController(
	// Репозиторий, который хранит информацию о всех регистрациях клиентов в данном приложении
	clientRegistrationRepository: ClientRegistrationRepository,

	// Сервис, который управляет уже авторизованными клиентами, для которых полчен ключ доступа
	authorizedClientService: OAuth2AuthorizedClientService
//	authorizedClientRepository: OAuth2AuthorizedClientRepository,
) {

	private lateinit var restClient: RestClient

	/**
	 * Нужно для предоставления полномочий при помощи кода доступа "greetings-app-authorization-code"
	 * Если работаем в контексте запроса и можем перенаправить пользователя на страницу авторизации
	 */
//	private val authorizedClientManager: OAuth2AuthorizedClientManager = DefaultOAuth2AuthorizedClientManager(
//		clientRegistrationRepository, authorizedClientRepository
//	)

	/**
	 * Получение кода доступа вне контекста http запроса "greetings-app-client-credentials" (межсервисное взаимодействие)
	 * и нет необходимости перенаправлять пользователя на страницу авторизации
	 */
	private val authorizedClientManager: OAuth2AuthorizedClientManager =
		AuthorizedClientServiceOAuth2AuthorizedClientManager(
			clientRegistrationRepository, authorizedClientService
		)

	init {
		restClient = RestClient.builder()
			.baseUrl("http://localhost:8081")
			.requestInterceptor { request, body, execution ->
				if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
					val token = authorizedClientManager.authorize(
//						OAuth2AuthorizeRequest.withClientRegistrationId("greetings-app-authorization-code")
						OAuth2AuthorizeRequest.withClientRegistrationId("greetings-app-client-credentials")
							.principal("greeting-app")
//							.principal(SecurityContextHolder.getContext().authentication)
							.build()
					)?.accessToken?.tokenValue

					token?.let {
						request.headers.setBearerAuth(it)
					}

				}
				execution.execute(request, body)
			}
			.build()
	}

//	@ModelAttribute("principal")
//	fun principal(principal: Principal): Principal {
//		return principal
//	}

	@GetMapping("/srv")
	fun getGreetingsPage(model: Model): String {
		model.addAttribute(
			"greetings",
			restClient.get()
				.uri("/greetings")
				.retrieve()
				.body(Greetings::class.java)
		)
		return "greetings"
	}
}
