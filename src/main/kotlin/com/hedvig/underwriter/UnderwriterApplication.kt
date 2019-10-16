package com.hedvig.underwriter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UnderwriterApplication

fun main(args: Array<String>) {
    runApplication<UnderwriterApplication>(*args)
}
