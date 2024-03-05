package ru.nb.server.ctrl

fun main() {
	val list = listOf("ROLE_USER", "SYSTEM", "ROLE_ADMIN")
	println(list.filter { it.startsWith("ROLE_") })

	println(0.takeIf { it > 0 }) // null
	println(5.takeIf { it > 0 })  // 5

	println(0.takeUnless { it == 0 }) // null
	println(5.takeUnless { it == 0 })  // 5
}