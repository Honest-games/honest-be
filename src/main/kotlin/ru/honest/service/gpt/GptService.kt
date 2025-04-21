package ru.honest.service.gpt

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.honest.mybatis.model.QuestionModel

@Service
class GptService(
    private val gptClient: GptClient,
) {
    fun createQuestionFromSimilar(similarQuestions: List<QuestionModel>): String {
        val promptPrefix = "Напиши вопрос, похожий по теме на эти вопросы, но не повторяющий ни один из них:\n"
        val question = "$promptPrefix ${similarQuestions.joinToString(";\n ") {it.text}} "
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