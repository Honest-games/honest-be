package ru.honest.config

import jakarta.annotation.PostConstruct
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class HonestProps(
    @Value("\${honest.last-card-text}")
    val lastCardText: String,
)