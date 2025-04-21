package ru.honest.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class HonestProps(
    @Value("\${honest.last-card-text}")
    val lastCardText: String,
    @Value("\${honest.sub-path}")
    val subPath: String,
    @Value("\${honest.gpt.question-request-prefix}")
    val gptQuestionRequestPrefix: String,
)