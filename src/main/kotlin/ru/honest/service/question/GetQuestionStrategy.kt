package ru.honest.service.question

import ru.honest.service.GenQuestionContext
import ru.honest.service.QuestionsService.GetQuestionAnswer

interface GetQuestionStrategy {
    fun getQuestion(genQuestionContext: GenQuestionContext): GetQuestionAnswer

    fun shouldBeUsed(genQuestionContext: GenQuestionContext): Boolean
}