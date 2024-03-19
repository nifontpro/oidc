package ru.nb.serverflux.ctrl

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.nb.oidc.base.Greetings
import java.security.Principal

@RestController
class TestController {

	@GetMapping("greetings")
	fun greetings(principal: Principal): Greetings {
		val name = principal.name ?: "Гость"
		return Greetings(greeting = "Привет, $name")
	}

}