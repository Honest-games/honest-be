package ru.honest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.mybatis.repo.QuestionModel
import ru.honest.mybatis.repo.QuestionsRepo

@RestController
@RequestMapping("/api/v1/questions")
class QuestionsController(
    private val questionsRepo: QuestionsRepo,
) {
    @GetMapping
    fun getQuestions(
        @RequestParam(required = true) levelId: String
    ): List<QuestionModel> {
        return questionsRepo.getQuestionsByLevel(levelId)
    }
}