package ru.honest.service.question

import ru.honest.service.QuestionsService.GetQuestionAnswer
import ru.honest.service.dto.GenQuestionContext

interface GetQuestionStrategy {
    fun getQuestion(genQuestionContext: GenQuestionContext): GetQuestionAnswer

    fun shouldBeUsed(genQuestionContext: GenQuestionContext): Boolean
}