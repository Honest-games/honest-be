package ru.honest.service.gpt

import org.springframework.stereotype.Component

@Component
class GptClientStub : GptClient {
    override fun chatCompletion(messages: List<ChatMessage>): ChatResponse {
        return ChatResponse(
            id = "1",
            created = 1,
            model = "any",
            choices = listOf(Choice(
                index = 0,
                message = Message(role = "assistant", content = "HELLO!"),
                finishReason = "stop"
            )),
            usage = null
        )
    }
}