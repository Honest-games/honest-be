package ru.honest.controller

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.config.HonestProps
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.factory.QuestionsFactory
import ru.honest.mybatis.repo.QuestionsHistoryRepo
import ru.honest.service.gpt.GptClientStub
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class QuestionsControllerTest(
    private val questionsController: QuestionsController,
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val honestProps: HonestProps,
    private val historyRepo: QuestionsHistoryRepo,
    private val gptClientStub: GptClientStub,
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
            val nonLastQuestion = getQuestion()
            gotIds.add(nonLastQuestion.id)
            assertFalse(nonLastQuestion.isLast)
            if(gotIds.size == questions.size) {
                assertEquals(questions.map { it.id }.toSet(), gotIds)
                val lastQuestion = getQuestion()
                assertTrue(lastQuestion.isLast)
                assertEquals("-1", lastQuestion.id)
                gotIds.clear()
            }
        }
    }

    @Test
    fun `history writes`(){
        val deck = decksFactory.createDeck()
        val level = levelsFactory.createLevel(deck)
        val questionsCount = 3
        val questions = List(questionsCount) { questionsFactory.createQuestion(level) }
        val playTimes = 5
        val clientId = "clientId"

        List(questionsCount * playTimes + playTimes) { questionsController.getRandomQuestion(level.id, clientId = clientId) }

        val history = historyRepo.findAll().map { Triple(it.clientId, it.questionId, it.levelId) }
        val expectedHistory = questions.flatMap { q ->
            List(playTimes) {q}.map { Triple(clientId, q.id, level.id) }
        }
        assertEquals(expectedHistory.sortedBy { it.second }, history.sortedBy { it.second })
    }

    @Test
    fun `getRandom,level not exist - error`(){
        val deck = decksFactory.createDeck()
        val level = levelsFactory.createLevel(deck)
        val questionsCount = 3
        List(questionsCount) { questionsFactory.createQuestion(level) }

        val levelId = "nonExistLevel"
        val answer = getRandQuestionHttpRaw("clientId", levelId)
            .toEntity(HonestError::class.java)
        assertTrue(answer.statusCode.is4xxClientError) { "Status should be 4xx" }
        assertEquals("Level $levelId not found", answer.body!!.error)
    }

    companion object
}