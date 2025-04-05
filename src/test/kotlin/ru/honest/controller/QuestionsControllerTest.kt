package ru.honest.controller

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.service.QuestionsService

@TestConstructor(autowireMode = ALL)
class QuestionsControllerTest(
    private val quesController: QuestionsController,
): BaseTest() {
    @Test
    fun `returns random questions`() {

    }
}