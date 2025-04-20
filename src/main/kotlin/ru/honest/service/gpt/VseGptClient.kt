package ru.honest.service.gpt

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec

@Component
@Profile("gpt.vse")
@Primary
class VseGptClient: GptClient {
    @Value("\${gpt.api-key}")
    private lateinit var apiKey: String

    override fun chatCompletion(model: String, messages: List<ChatMessage>): Any {
        val body = ChatRequest(model = model, messages = messages)

        val retrieve = restClient().post()
            .uri("/chat/completions")
            .body(body)
            .retrieve()
        val status = retrieve.toEntity(String::class.java)
//        val response = retrieve
//            .handleGptErrors()
//            .body(ChatResponse::class.java)!!
//            .body(String::class.java)!!

        return status
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
}
