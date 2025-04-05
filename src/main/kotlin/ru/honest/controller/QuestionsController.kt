package ru.honest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.mybatis.model.QuestionModel
import ru.honest.service.QuestionsService

@RestController
@RequestMapping("/api/v1/questions")
class QuestionsController(
    private val questionsService: QuestionsService,
) {
    @GetMapping("/random")
    fun random(
        @RequestParam levelId: String,
        @RequestParam clientId: String,
    ): QuestionModel {
        //TODO тест на валидацию левела
        return questionsService.readRandomQuestion(levelId, clientId)
    }
}