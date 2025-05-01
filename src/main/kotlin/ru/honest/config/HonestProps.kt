package ru.honest.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.honest.toUtf8

@Component
data class HonestProps(
    @Value("\${honest.last-card-text}")
    private val _lastCardText: String,
    @Value("\${honest.sub-path}")
    val subPath: String,
    @Value("\${honest.gpt.questions-gen.prompt-template}")
    private val _gptGenQuestionPromptTemplate: String,
    @Value("\${honest.gpt.questions-gen.error-message}")
    private val _gptQuestionErrorMessage: String,
    @Value("\${honest.gpt.questions-gen.system-message}")
    private val _gptSystemMessage: String
){
    val lastCardText = _lastCardText.toUtf8()
    val gptGenQuestionPromptTemplate = _gptGenQuestionPromptTemplate.toUtf8()
    val gptQuestionErrorMessage = _gptQuestionErrorMessage.toUtf8()
    val gptSystemMessage = _gptSystemMessage.toUtf8()
}