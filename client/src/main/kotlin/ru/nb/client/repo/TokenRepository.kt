package ru.nb.client.repo

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Service

@Service
class TokenRepository(
	// Репозиторий, который хранит информацию о всех регистрациях клиентов в данном приложении
	clientRegistrationRepository: ClientRegistrationRepository,

	// Сервис, который управляет уже авторизованными клиентами, для которых получен ключ доступа
	authorizedClientService: OAuth2AuthorizedClientService
//	authorizedClientRepository: OAuth2AuthorizedClientRepository,
) {

	/**
	 * Получение кода доступа вне контекста http запроса "greetings-app-client-credentials" (межсервисное взаимодействие)
	 * и нет необходимости перенаправлять пользователя на страницу авторизации
	 */
	private val authorizedClientManager: OAuth2AuthorizedClientManager =
		AuthorizedClientServiceOAuth2AuthorizedClientManager(
			clientRegistrationRepository, authorizedClientService
		)

	fun getToken(): String? {
		return authorizedClientManager.authorize(
			OAuth2AuthorizeRequest.withClientRegistrationId("greetings-app-client-credentials")
				.principal("greeting-app")
				.build()
		)?.accessToken?.tokenValue
	}

}