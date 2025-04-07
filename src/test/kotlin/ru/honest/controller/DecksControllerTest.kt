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
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class DecksControllerTest(
    private val decksFactory: DecksFactory,
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

    fun getDecks(clientId: String? = null): List<DeckOutput> {
        val url = baseUrl() + "/api/v1/decks" +
                (("?clientId=$clientId").takeIf { clientId != null } ?: "")
        val restClient = RestClient.create()
        val body = restClient.get().uri(url).retrieve().body(object : ParameterizedTypeReference<List<DeckOutput>>() {})
        return body!!
    }
}