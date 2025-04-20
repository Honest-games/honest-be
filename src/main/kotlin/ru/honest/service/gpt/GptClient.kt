package ru.honest.service.gpt

interface GptClient {
    fun chatCompletion(model: String, messages: List<ChatMessage>): Any
}