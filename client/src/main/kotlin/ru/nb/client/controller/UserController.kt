package ru.nb.client.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping("user")
class UserController {

	@ModelAttribute("principal")
	fun principal(principal: Principal): Principal {
		return principal
	}

	@GetMapping
	fun getUserPage(): String = "user"
}
