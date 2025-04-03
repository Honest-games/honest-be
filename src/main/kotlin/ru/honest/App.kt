package ru.honest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.context.config.annotation.RefreshScope

@RefreshScope
@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
