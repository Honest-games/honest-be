package ru.honest.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.client.RestClient
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.factory.QuestionsFactory
import ru.honest.factory.VectorImageFactory
import ru.honest.mybatis.model.DeckAiType
import ru.honest.mybatis.model.DeckModel
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class DecksControllerTest(
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val questionsController: QuestionsController,
    private val levelsController: LevelsController,
    private val vectorImageFactory: VectorImageFactory,
): BaseTest() {
    @Test
    fun `without clientId - fail`() {
        assertThrows<BadRequest> {
            getDecks()
        }
    }

    @Test
    fun `success with clientId - gives correct num of decks`() {
        decksFactory.createDeck()
        decksFactory.createDeck()
        decksFactory.createDeck()
        val decks = getDecks("1")
        assertEquals(3, decks.size)
    }

    @Test
    fun `success with clientId - order correct`() {
        val deck3 = decksFactory.createDeck(order = 3)
        val deck1 = decksFactory.createDeck(order = 1)
        val deck2 = decksFactory.createDeck(order = 2)
        val decks = getDecks("1")

        assertEquals(deck1.id, decks[0].id)
        assertEquals(deck2.id, decks[1].id)
        assertEquals(deck3.id, decks[2].id)
    }

    @Test
    fun `success with clientId - gives correct deck data`() {
        val bgImageId = "bg_1"
        val modalImageId = "md_1"
        vectorImageFactory.createImage(bgImageId, "some")
        vectorImageFactory.createImage(modalImageId, "some2")

        val deck = decksFactory.createDeck(DeckModel(
            id = "1",
            name = "name",
            languageCode = "RUU",
            description = "desc",
            labels = "l1;l2",
            imageId = "image_1",
            hidden = false,
            promo = null,
            bgImageId = bgImageId,
            modalImageId = modalImageId,
            order = 1,
            aiType = DeckAiType.NON_AI,
        ))
        val decks = getDecks("1")
        assertEquals(DeckOutput(
            id = "1",
            languageCode = "RUU",
            name = "name",
            description = "desc",
            labels = listOf("l1", "l2"),
            imageId = "image_1",
            backgroundImageId = "bg_1",
            modalImageId = "md_1",
            aiType = DeckAiType.NON_AI,
        ), decks[0])
    }

    @Test
    fun `dont return hidden decks`() {
        decksFactory.createDeck(hidden = true)
        decksFactory.createDeck(hidden = true)

        assertEquals(0, getDecks("1").size)
    }

    @Test
    fun `returns unlocked hidden decks`() {
        val deck = decksFactory.createDeck(hidden = true, promo = "p0")
        val deck2 = decksFactory.createDeck(hidden = true, promo = "p1")
        val clientId = "1"

        enterPromo(clientId, "p1")

        assertEquals(0, getDecks("otherClient").size)
        assertEquals(1, getDecks(clientId).size)
    }

    @Test
    fun `shuffle, wrong levelId - 4xx`(){
        assertThrows<BadRequest> {
            shuffleDeck("some", "1")
        }
    }

    @Test
    fun `shuffle, wrong clientId - 4xx`(){
        assertThrows<BadRequest> {
            shuffleDeck("some")
        }
    }

    @Test
    fun `shuffle - shuffles`(){
        val deck1 = decksFactory.createDeck()
        val deck2 = decksFactory.createDeck()

        val decksOfLevels = listOf(deck1, deck2).map { deck -> List(3){levelsFactory.createLevel(deck)} }
        val questions = decksOfLevels.map { it.map { level -> List(3){questionsFactory.createQuestion(level)} } }
        val clientId = "1"

        // get 1 question from each level
        decksOfLevels.flatten().forEach { questionsController.getRandomQuestion(it.id, clientId) }

        shuffleDeck(deck1.id, clientId)

        val levels1 = levelsController.getLevels(clientId, deck1.id)
        val levels2 = levelsController.getLevels(clientId, deck2.id)

        // shuffled
        levels1.forEach { assertEquals(0, it.counts.openedQuestionsCount) }
        // not shuffled
        levels2.forEach { assertEquals(1, it.counts.openedQuestionsCount) }

        // get all 3 questions from shuffled level
        val gotQuestions = levels1.map { level -> List(3) { questionsController.getRandomQuestion(level.id, clientId) } }

        assertEquals(
            questions[0].flatten().map { it.id }.sorted(),
            gotQuestions.flatten().map { it.id }.sorted()
        )
    }

    fun shuffleDeck(
        deckId: String,
        clientId: String? = null,
    ) {
        val url = baseUrl() + "/api/v1/decks/$deckId/shuffle" +
                (("?clientId=$clientId").takeIf { clientId != null } ?: "")
        RestClient.create().post().uri(url).retrieve().body(String::class.java)
    }
}