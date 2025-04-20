package ru.honest.service.gpt

import org.springframework.stereotype.Component

@Component
class GptClientStub : GptClient {
    override fun chatCompletion(model: String, messages: List<ChatMessage>): Any {
        return "model $model used for messages $messages"
    }
}