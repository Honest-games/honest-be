package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper

@Mapper
interface QuestionsRepo {
    fun getQuestionsByLevel(
        levelId: String
    ): List<QuestionModel>

    fun save(question: QuestionModel)
}

data class QuestionModel(
    val id: String,
    val levelId: String,
    val text: String,
    val additionalTest: String?,
)
