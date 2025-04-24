package ru.honest.service.gpt

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec
import org.springframework.web.client.bodyWithType

@Component
@Profile("gpt.vse")
@Primary
class VseGptClient: GptClient {
    @Value("\${gpt.api-key}")
    private lateinit var apiKey: String

    @Value("\${gpt.model}")
    private lateinit var model: String

    override fun chatCompletion(messages: List<ChatMessage>): ChatResponse {
        val body = ChatRequest(model = model, messages = messages)
        return restClient().post()
            .uri("/chat/completions")
            .bodyWithType<ChatRequest>(body)
            .retrieve()
            .handleGptErrors()
            .body(ChatResponse::class.java)!!
    }

    private fun restClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://api.vsegpt.ru/v1")
            .defaultHeader("Authorization", "Bearer $apiKey")
            .defaultHeader("X-Title", "Honest")
            .requestFactory(BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()))
            .build()
    }

    private fun ResponseSpec.handleGptErrors(): ResponseSpec {
        return this
            .onStatus({ it.value() == 429 }) { _, response ->
                val msg = response.body.readAllBytes().decodeToString()
                throw GptRateLimitException("Rate limit exceeded: $msg")
            }
            .onStatus( { status -> status.value() == 500 })
            { _, response ->
                val msg = response.body.readAllBytes().decodeToString()
                throw GptServerErrorException("Internal Server Error: $msg")
            }
            .onStatus({ status -> status.value() == 401 })
            { _, response ->
                val msg = response.body.readAllBytes().decodeToString()
                throw GptUnauthorizedException("Unauthorized: $msg")
            }
            .onStatus ({ status -> status.is4xxClientError })
            { _, response ->
                val msg = response.body.readAllBytes().decodeToString()
                throw GptClientErrorException("Code: ${response.statusCode.value()}. Client Error: $msg")
            }
    }
}
