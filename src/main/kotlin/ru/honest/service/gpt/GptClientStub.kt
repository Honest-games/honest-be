package ru.honest.service.gpt

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("honest.gpt.service", havingValue = "stub")
class GptClientStub : GptClient {
    private var stubResponse: String? = null

    override fun chatCompletion(messages: List<ChatMessage>): ChatResponse {
        return ChatResponse(
            id = "1",
            created = 1,
            model = "any",
            choices = listOf(Choice(
                index = 0,
                message = Message(role = "assistant", content = requireNotNull(stubResponse)),
                finishReason = "stop"
            )),
            usage = null
        )
    }

    fun setStubResponse(stubResponse: String) {
        this.stubResponse = stubResponse
    }
}