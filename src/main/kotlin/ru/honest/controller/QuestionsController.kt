package ru.honest.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.service.QuestionsService

@Tag(name = "default")
@RestController
@RequestMapping("/api/v1/questions")
class QuestionsController(
    private val questionsService: QuestionsService,
) {
    @GetMapping("/random")
    fun getRandomQuestion(
        @RequestParam levelId: String,
        @RequestParam clientId: String,
    ): QuestionOutput {
        val answer = questionsService.readRandomQuestion(levelId, clientId)
        return QuestionOutput.create(answer.question, answer.isLast)
    }
}