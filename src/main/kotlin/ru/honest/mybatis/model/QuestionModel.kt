package ru.honest.mybatis.model

data class QuestionModel(
    val id: String,
    val levelId: String,
    val text: String,
    val additionalText: String?,
)