package ru.honest.controller

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.config.HonestProps
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.factory.QuestionsFactory
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class QuestionsControllerTest(
    private val questionsController: QuestionsController,
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val honestProps: HonestProps
) : BaseTest() {
    @Test
    fun `returns random questions and last card for many clients`() {
        val deck = decksFactory.createDeck()
        val levelsToQuestions = List(3) {it+1}.map {
            val level = levelsFactory.createLevel(deck)
            val questions = List(it) {}.map {
                questionsFactory.createQuestion(level)
            }
            Pair(level, questions)
//        }.associateBy { it.first }
        }.associateBy({ it.first }, { it.second })

        val levelsIdsToQuestions = levelsToQuestions.mapKeys { it.key.id }

        // клиентов 3
        //клиент берет вопрос на каждом уровне по вопросовНаУровне*2+1 раза
        //для каждого клиента+уровня должно собраться вопросыУровня*2 и ласт карта.
        // след карта должна быть ласт

        val clients = setOf("1", "2", "3")
        val clientToLevels = clients.flatMap { clientId ->
            levelsToQuestions.entries.map { Pair(clientId, it.key.id) }
        }
        val questionRequests = clientToLevels.map { clientToLevel ->
            val questions = levelsIdsToQuestions[clientToLevel.second]!!
            val levelTries = questions.size * 2 + 2
            List(levelTries) { clientToLevel.copy() }
        }.flatten().shuffled()

        val clientToGotQuestions = questionRequests.map {
            Pair(it.first, questionsController.getRandomQuestion(it.second, it.first))
        }.groupBy({ it.first }, { it.second })
        val expectedQuestionsIdsForClient = levelsToQuestions.values.flatMap { questions ->
            val ids = questions.map { it.id }
            listOf(ids, ids, listOf("-1", "-1")).flatten()
        }
        clientToGotQuestions.forEach { (_, gotQuestions) ->
            assertEquals(expectedQuestionsIdsForClient.sorted(), gotQuestions.map { it.id }.sorted())
        }
    }

    @Test
    fun `last card goes only last`() {
        val deck = decksFactory.createDeck()
        val level = levelsFactory.createLevel(deck)
        val questions = List(4) { questionsFactory.createQuestion(level) }
        val getQuestion = { questionsController.getRandomQuestion(level.id, clientId = "1") }
        val playTimes = 5
        val gotIds = mutableSetOf<String>()

        List((questions.size+1) * playTimes) {
            gotIds.add(getQuestion().id)
            if(gotIds.size == questions.size) {
                assertEquals(questions.map { it.id }.toSet(), gotIds)
                assertEquals("-1", getQuestion().id)
                gotIds.clear()
            }
        }
    }
}