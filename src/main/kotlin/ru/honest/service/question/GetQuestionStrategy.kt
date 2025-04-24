package ru.honest.service.question

import ru.honest.service.QuestionsService.GetQuestionAnswer

interface GetQuestionStrategy {
    fun getQuestion(
        levelId: String,
        clientId: String,
        ai: Boolean = false,
    ): GetQuestionAnswer

    fun shouldBeUsed(
        levelId: String,
        clientId: String,
        ai: Boolean = false,
    ): Boolean
}