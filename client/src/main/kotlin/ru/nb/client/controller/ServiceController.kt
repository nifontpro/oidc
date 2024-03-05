package ru.nb.client.controller

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestClient
import ru.nb.client.repo.TokenRepository
import ru.nb.oidc.base.Greetings

@Controller
class ServiceController(
	tokenRepository: TokenRepository
) {

	private val restClient: RestClient = RestClient.builder()
		.baseUrl("http://localhost:8081")
		.requestInterceptor { request, body, execution ->
			if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {

				tokenRepository.getToken()?.let {
					request.headers.setBearerAuth(it)
				}

			}
			execution.execute(request, body)
		}
		.build()

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
