package ru.nb.client.repo

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class AsyncWebClient(
	private val tokenRepository: TokenRepository,
	@Value("\${resource.server.url}") private val resourceServerURL: String,
) {

	private val webClient: WebClient = WebClient.create(resourceServerURL)

	suspend fun <R> getDataFromMs(
		uri: String,
//		requestBody: Any,
		responseType: ParameterizedTypeReference<R>
	): R {
		val accessToken = tokenRepository.getToken()

		return webClient
//			.post()
			.get()
			.uri(uri)
//			.bodyValue(requestBody)
			.headers {
				it.addAll(HttpHeaders().apply {
					accessToken?.let { token -> setBearerAuth(token) }
				})
			}
			.retrieve()
			.bodyToMono(responseType)
			.awaitSingle()
	}
}

inline fun <reified R> getResponseType() = object : ParameterizedTypeReference<R>() {}