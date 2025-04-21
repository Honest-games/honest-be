package ru.honest.service.gpt

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.honest.config.HonestProps
import ru.honest.mybatis.model.QuestionModel

@Service
class GptService(
    private val gptClient: GptClient,
    private val honestProps: HonestProps,
) {
    fun createQuestionFromSimilar(similarQuestions: List<QuestionModel>): String {
        val question = "${honestProps.gptQuestionRequestPrefix} ${similarQuestions.joinToString(";\n ") { it.text }} "
        val messages = listOf(
            ChatMessage(role = GptChatRole.USER, content = question),
        )
        try {
            val response = gptClient.chatCompletion(messages)
            return response.choices.firstOrNull()?.message?.content ?: throw IllegalStateException("Empty gpt response")
        } catch (e: Exception) {
            logger.error(e.message, e)
            return "Ошибка при генерации вопроса, попробуйте позже"
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GptService::class.java)
    }
}