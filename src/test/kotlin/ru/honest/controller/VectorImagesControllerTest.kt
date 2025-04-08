package ru.honest.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.client.HttpClientErrorException.NotFound
import org.springframework.web.client.RestClient
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.VectorImageFactory
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class VectorImagesControllerTest(
    private val vectorImageFactory: VectorImageFactory,
): BaseTest() {
    @Test
    fun `without id - not found`() {
        assertThrows<NotFound> {
            getImage()
        }
    }

    @Test
    fun `with wrong id - 4xx`() {
        assertThrows<BadRequest> {
            getImage("wrong-id")
        }
    }

    @Test
    fun `with id - gives image`() {
        val content = "<svg></svg>"
        val id = "1"
        vectorImageFactory.createImage(id, content)
        vectorImageFactory.createImage(id+"_", content+"_")

        assertEquals(content, getImage(id))
    }

    fun getImage(id: String? = null): String {
        val url = baseUrl() + "/api/v1/vector-images/" + (id.takeIf { it != null } ?: "")
        val restClient = RestClient.create()
        val body = restClient.get().uri(url).retrieve().body(String::class.java)
        return body!!
    }
}