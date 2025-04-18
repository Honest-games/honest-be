package ru.honest.service.gpt

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec

@Component
class VseGptClient: GptClient {
    @Value("\${gpt.api-key}")
    private lateinit var apiKey: String

    override fun chatCompletion(model: GptModel, messages: List<ChatMessage>): Any {
        val body = ChatRequest(model = model.modelName, messages = messages)

        val response = restClient().post()
            .uri("/chat/completions")
            .body(body)
            .retrieve()
            .handleGptErrors()
            .body(ChatResponse::class.java)!!

        return response
    }

    private fun restClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://api.vsegpt.ru/v1")
            .defaultHeader("Authorization", "Bearer $apiKey")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)

//            .defaultHeader("X-Title", "MyApp")
            .build()
    }

    private fun ResponseSpec.handleGptErrors(): ResponseSpec {
        return this
            .onStatus({ it.value() == 429 }) { _, response ->
                val msg = response.body.toString()
                throw GptRateLimitException("Rate limit exceeded: $msg")
            }
            .onStatus( { status -> status.value() == 500 })
            { _, response ->
                val msg = response.body.toString()
                throw GptServerErrorException("Internal Server Error: $msg")
            }
            .onStatus({ status -> status.value() == 401 })
            { _, response ->
                val msg = response.body.toString()
                throw GptUnauthorizedException("Unauthorized: $msg")
            }
            .onStatus ({ status -> status.is4xxClientError })
            { _, response ->
                val msg = response.body.toString()
                throw GptClientErrorException("Client Error: $msg")
            }
    }


    class GptRateLimitException(message: String?) : RuntimeException(message)
    class GptServerErrorException(message: String?) : RuntimeException(message)
    class GptUnauthorizedException(message: String?) : RuntimeException(message)
    class GptClientErrorException(message: String?) : RuntimeException(message)

    data class ChatMessage(val role: String, val content: Any)

    data class ChatRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Double = 0.7,
        @JsonProperty("max_tokens")
        val maxTokens: Int = 1000,
        val n: Int = 1
    )

    data class ChatResponse(
        val id: String,
        val created: Long,
        val model: String,
        val choices: List<Choice>,
        val usage: Usage?
    )

    data class Choice(
        val index: Int,
        val message: Message,
        @JsonProperty("finish_reason")
        val finishReason: String
    )

    data class Message(
        val role: String,
        val content: String
    )

    data class Usage(
        @JsonProperty("prompt_tokens")
        val promptTokens: Int,
        @JsonProperty("completion_tokens")
        val completionTokens: Int,
        @JsonProperty("total_tokens")
        val totalTokens: Int
    )

    enum class GptModel(val modelName: String) {
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_4("gpt-4"),
        GPT_4_32K("gpt-4-32k");
    }
}
