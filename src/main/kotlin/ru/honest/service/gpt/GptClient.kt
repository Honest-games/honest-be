package ru.honest.service.gpt

import ru.honest.service.gpt.VseGptClient.ChatMessage
import ru.honest.service.gpt.VseGptClient.GptModel

interface GptClient {
    fun chatCompletion(model: GptModel, messages: List<ChatMessage>): Any
}