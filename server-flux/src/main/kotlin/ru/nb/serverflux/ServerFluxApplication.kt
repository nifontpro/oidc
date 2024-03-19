package ru.nb.serverflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerFluxApplication

fun main(args: Array<String>) {
	runApplication<ServerFluxApplication>(*args)
}
