package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.QuestionModel

@Mapper
interface QuestionsRepo {
    fun getQuestionsByLevel(
        levelId: String
    ): List<QuestionModel>

    fun save(question: QuestionModel)

    fun getByParams(
        levelsIds: List<String>? = null
    ): List<QuestionModel>

    fun getAlreadyAiGeneratedQuestions(
        levelId: String,
        clientId: String
    ): List<String>

    fun addAiGeneratedQuestionToHistory(
        questionText: String,
        levelId: String,
        clientId: String
    )
}

