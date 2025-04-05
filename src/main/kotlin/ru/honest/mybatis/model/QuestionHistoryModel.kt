package ru.honest.mybatis.model

import java.time.LocalDateTime

data class QuestionHistoryModel(
    val id: String,
    val levelId: String,
    val questionId: String,
    val clientId: String,
    val time: LocalDateTime,
)
