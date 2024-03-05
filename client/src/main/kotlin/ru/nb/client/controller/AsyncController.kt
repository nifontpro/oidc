package ru.nb.client.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.nb.client.repo.AsyncWebClient
import ru.nb.client.repo.getResponseType
import ru.nb.oidc.base.Greetings

@RestController
class AsyncController(
	private val asyncWebClient: AsyncWebClient
) {

	@GetMapping("test")
	suspend fun test(): Greetings {

		return asyncWebClient.getDataFromMs(
			uri = "greetings",
			responseType = getResponseType()
		)
	}
}
