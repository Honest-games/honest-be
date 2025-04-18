package ru.honest.service.gpt

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("test")
@Primary
class GptClientStub : GptClient {
    override fun chatCompletion(model: VseGptClient.GptModel, messages: List<VseGptClient.ChatMessage>): Any {
        return "model $model used for messages $messages"
    }
}