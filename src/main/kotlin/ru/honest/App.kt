package ru.honest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HonestBeApplication

fun main(args: Array<String>) {
    runApplication<HonestBeApplication>(*args)
}
