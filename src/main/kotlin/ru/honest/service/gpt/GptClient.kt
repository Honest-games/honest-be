package ru.honest.service.gpt

interface GptClient {
    fun chatCompletion(messages: List<ChatMessage>): ChatResponse
}