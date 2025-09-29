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
import ru.honest.factory.PromoFactory
import ru.honest.factory.QuestionsFactory
import ru.honest.mybatis.model.enums.PromoType
import ru.honest.mybatis.repo.QuestionsHistoryRepo
import ru.honest.service.gpt.GptClientStub
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class QuestionsControllerTest(
    private val questionsController: QuestionsController,
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val promoFactory: PromoFactory,
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

    @Test
    fun `returns purchase required after 15 questions per deck`() {
        val deck = decksFactory.createDeck()
        val level = levelsFactory.createLevel(deck)
        val questions = List(20) { questionsFactory.createQuestion(level) }
        val clientId = "testClient"

        // Получаем первые 15 вопросов из этой колоды - должны быть нормальными
        repeat(15) {
            val response = questionsController.getRandomQuestion(level.id, clientId)
            assertFalse(response.isPurchaseRequired) { "First 15 questions from this deck should not require purchase" }
        }

        // 16-й вопрос из этой же колоды должен требовать покупку
        val purchaseResponse = questionsController.getRandomQuestion(level.id, clientId)
        assertTrue(purchaseResponse.isPurchaseRequired) { "16th question from same deck should require purchase" }
        assertEquals("-2", purchaseResponse.id)
        assertTrue(purchaseResponse.text.contains("15 бесплатных вопросов"))
    }

    @Test
    fun `allows unlimited questions with full access promo`() {
        val deck = decksFactory.createDeck()
        val level = levelsFactory.createLevel(deck)
        val questions = List(20) { questionsFactory.createQuestion(level) }
        val clientId = "testClientWithPromo"
        val promoCode = "FULL_ACCESS_PROMO"

        // Создаем промо-код полного доступа
        promoFactory.createPromo(
            deckId = deck.id,
            promoType = PromoType.DECK_FULL_ACCESS,
            promoCode = promoCode
        )

        // Активируем промо-код
        enterPromo(clientId, promoCode)

        // Должны получить больше 15 вопросов без ограничений
        repeat(20) {
            val response = questionsController.getRandomQuestion(level.id, clientId)
            assertFalse(response.isPurchaseRequired) { "Questions with full access promo should not require purchase" }
        }
    }

    @Test
    fun `limits are independent per deck`() {
        // Создаем две разные колоды
        val deckA = decksFactory.createDeck()
        val levelA = levelsFactory.createLevel(deckA)
        val questionsA = List(20) { questionsFactory.createQuestion(levelA) }

        val deckB = decksFactory.createDeck()
        val levelB = levelsFactory.createLevel(deckB)
        val questionsB = List(20) { questionsFactory.createQuestion(levelB) }

        val clientId = "crossDeckClient"

        // Получаем 10 вопросов из колоды A
        repeat(10) {
            val response = questionsController.getRandomQuestion(levelA.id, clientId)
            assertFalse(response.isPurchaseRequired) { "First 10 questions from deck A should not require purchase" }
        }

        // Получаем 10 вопросов из колоды B - должны быть доступны!
        repeat(10) {
            val response = questionsController.getRandomQuestion(levelB.id, clientId)
            assertFalse(response.isPurchaseRequired) { "First 10 questions from deck B should not require purchase despite deck A usage" }
        }

        // Еще 5 вопросов из колоды A - все еще доступны (всего 15 из A)
        repeat(5) {
            val response = questionsController.getRandomQuestion(levelA.id, clientId)
            assertFalse(response.isPurchaseRequired) { "Questions 11-15 from deck A should still be available" }
        }

        // 16-й вопрос из колоды A - должен требовать покупку
        val purchaseResponseA = questionsController.getRandomQuestion(levelA.id, clientId)
        assertTrue(purchaseResponseA.isPurchaseRequired) { "16th question from deck A should require purchase" }

        // Но колода B все еще должна быть доступна! (всего 10 из B)
        repeat(5) {
            val response = questionsController.getRandomQuestion(levelB.id, clientId)
            assertFalse(response.isPurchaseRequired) { "Questions 11-15 from deck B should still be available" }
        }

        // 16-й вопрос из колоды B - теперь должен требовать покупку
        val purchaseResponseB = questionsController.getRandomQuestion(levelB.id, clientId)
        assertTrue(purchaseResponseB.isPurchaseRequired) { "16th question from deck B should require purchase" }
    }

    @Test
    fun `promo code works only for specific deck`() {
        // Создаем две разные колоды
        val deckA = decksFactory.createDeck()
        val levelA = levelsFactory.createLevel(deckA)
        val questionsA = List(20) { questionsFactory.createQuestion(levelA) }

        val deckB = decksFactory.createDeck()
        val levelB = levelsFactory.createLevel(deckB)
        val questionsB = List(20) { questionsFactory.createQuestion(levelB) }

        val clientId = "promoTestClient"
        val promoCodeA = "DECK_A_PROMO"

        // Исчерпываем лимит в обеих колодах
        repeat(15) {
            questionsController.getRandomQuestion(levelA.id, clientId)
            questionsController.getRandomQuestion(levelB.id, clientId)
        }

        // Проверяем, что обе колоды заблокированы
        val blockedA = questionsController.getRandomQuestion(levelA.id, clientId)
        val blockedB = questionsController.getRandomQuestion(levelB.id, clientId)
        assertTrue(blockedA.isPurchaseRequired) { "Deck A should be blocked after 15 questions" }
        assertTrue(blockedB.isPurchaseRequired) { "Deck B should be blocked after 15 questions" }

        // Создаем промо-код ТОЛЬКО для колоды A
        promoFactory.createPromo(
            deckId = deckA.id,
            promoType = PromoType.DECK_FULL_ACCESS,
            promoCode = promoCodeA
        )

        // Активируем промо-код
        enterPromo(clientId, promoCodeA)

        // Колода A должна разблокироваться
        repeat(5) {
            val response = questionsController.getRandomQuestion(levelA.id, clientId)
            assertFalse(response.isPurchaseRequired) { "Deck A should be unblocked after promo activation" }
        }

        // Колода B должна остаться заблокированной!
        val stillBlockedB = questionsController.getRandomQuestion(levelB.id, clientId)
        assertTrue(stillBlockedB.isPurchaseRequired) { "Deck B should remain blocked - promo was only for deck A" }
    }

    companion object
}