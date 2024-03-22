package ru.nb.serverflux.ctrl

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.nb.oidc.base.Greetings
import java.security.Principal

@RestController
class TestController {

	@GetMapping("greetings")
	fun greetings(
		principal: Principal,
//		@AuthenticationPrincipal userDetails: UserDetails?,
	): Greetings {
		val name = principal.name ?: "Гость"
//		val name = userDetails?.username ?: "Гость"
//		println(userDetails)
		return Greetings(greeting = "Привет, $name")
	}

}