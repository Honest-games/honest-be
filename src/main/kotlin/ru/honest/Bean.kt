package ru.honest

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class Bean {
    @Value("\${ru.honest.prop}")
    private lateinit var prop: String

    @PostConstruct
    fun init() {
        println(prop)
        println(prop)
        println(prop)
        println(prop)
    }
}