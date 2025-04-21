package ru.honest.service.gpt

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

class GptRateLimitException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException()
class GptServerErrorException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException()
class GptUnauthorizedException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException()
class GptClientErrorException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException()

data class ChatMessage(val role: GptChatRole, val content: Any)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
//    val temperature: Double = 0.7,
//    @JsonProperty("max_tokens")
//    val maxTokens: Int = 1000,
//    val n: Int = 1
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
    val totalTokens: Int,
    @JsonProperty("total_cost")
    val totalCost: Double,
)

enum class GptModel(val modelName: String) {
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_4("gpt-4"),
    GPT_4_32K("gpt-4-32k"),
    GPT_4O_MINI("openai/gpt-4o-mini"),
    ;

    companion object {
        fun fromModelName(modelName: String): GptModel {
            return values().firstOrNull { it.modelName == modelName }
                ?: throw IllegalArgumentException("Unknown model name: $modelName")
        }
    }
}

enum class GptChatRole(private val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;
    @JsonValue
    override fun toString(): String = value
}