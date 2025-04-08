package ru.honest.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.client.RestClient
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.factory.QuestionsFactory
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class LevelsControllerTest(
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val questionsController: QuestionsController,
) : BaseTest() {
    @Test
    fun `without clientId - fail`() {
        assertThrows<BadRequest> {
            getLevels()
        }
    }

    @Test
    fun `without deckId - fail`() {
        assertThrows<BadRequest> {
            getLevels(clientId = "1")
        }
    }

    @Test
    fun `success with clientId and deckId - gives correct num of decks`() {
        val deck = decksFactory.createDeck()
        val expected = listOf(
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
        ).map { LevelOutput.create(it, 0, 0) }

        levelsFactory.createLevel()
        levelsFactory.createLevel()
        levelsFactory.createLevel()

        val given = getLevels(clientId = "1", deckId = deck.id)
        assertEquals(expected, given)
    }

    @Test
    fun `cards count shown correctly`() {
        val deck = decksFactory.createDeck()
        val levels = listOf(
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
        )
        val otherLevel = levelsFactory.createLevel()
        val otherQuestion = questionsFactory.createQuestion(otherLevel)

        List(3){it}.map { index ->
            List(index+1){questionsFactory.createQuestion(levels[index])}
        }
        val given = getLevels("1", deck.id)
        val expected = listOf(
            LevelOutput.create(levels[0], 1, 0),
            LevelOutput.create(levels[1], 2, 0),
            LevelOutput.create(levels[2], 3, 0),
        )
        assertEquals(expected, given)
    }

    @Test
    fun `opened cards count shown correctly`() {
        val clientId = "1"
        val deckId = "deckId"
        fun getOpenedCounts(): List<Int> = getLevels(clientId, deckId).map { it.counts.openedQuestionsCount }
        fun read(levelId: String, specifiedClientId: String? = null) =
            questionsController.getRandomQuestion(levelId, specifiedClientId ?: clientId)

        val otherDeck = decksFactory.createDeck()
        val otherQuestion = questionsFactory.createQuestion(levelsFactory.createLevel(otherDeck))
        questionsController.getRandomQuestion(otherQuestion.levelId, clientId = clientId)

        val deck = decksFactory.createDeck(deckId)

        val level1 = levelsFactory.createLevel(deck)
        val level2 = levelsFactory.createLevel(deck)

        val q11 = questionsFactory.createQuestion(level1)
        val q12 = questionsFactory.createQuestion(level1)
        val q13 = questionsFactory.createQuestion(level1)

        val q21 = questionsFactory.createQuestion(level2)
        val q22 = questionsFactory.createQuestion(level2)

        read(level1.id, "2")
        read(level2.id, "2")

        assertEquals(listOf(0,0), getOpenedCounts())
        read(level1.id)
        assertEquals(listOf(1,0), getOpenedCounts())
        read(level2.id)
        assertEquals(listOf(1,1), getOpenedCounts())
        read(level2.id)
        assertEquals(listOf(1,2), getOpenedCounts())
        read(level2.id)
        assertEquals(listOf(1,0), getOpenedCounts())
        read(level1.id)
        read(level1.id)
        assertEquals(listOf(3,0), getOpenedCounts())
        read(level1.id)
        assertEquals(listOf(0,0), getOpenedCounts())
    }

    fun getLevels(
        clientId: String? = null,
        deckId: String? = null,
    ): List<LevelOutput> {
        val url = baseUrl() + "/api/v1/levels" +
                (("?clientId=$clientId").takeIf { clientId != null } ?: "") +
                (("&deckId=$deckId").takeIf { deckId != null } ?: "")
        val restClient = RestClient.create()
        val body = restClient.get().uri(url).retrieve().body(object : ParameterizedTypeReference<List<LevelOutput>>() {})
        return body!!
    }
}