package ru.honest

import org.flywaydb.core.Flyway
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestClient
import ru.honest.controller.DeckOutput
import ru.honest.controller.QuestionOutput

@SpringBootTest(webEnvironment = RANDOM_PORT)
@FlywayTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class BaseTest {
    @LocalServerPort
    var port: Int = 0

    val scheme = "http"

    fun baseUrl(): String = "$scheme://localhost:$port"

    @Autowired
    private lateinit var flyway: Flyway

    @BeforeEach
    fun setup() {
        flyway.clean()
        flyway.migrate()
    }

    fun getDecks(clientId: String? = null): List<DeckOutput> {
        val url = baseUrl() + "/api/v1/decks" +
                (("?clientId=$clientId").takeIf { clientId != null } ?: "")
        val restClient = RestClient.create()
        val body = restClient.get().uri(url).retrieve().body(object : ParameterizedTypeReference<List<DeckOutput>>() {})
        return body!!
    }

    fun enterPromo(clientId: String?, promo: String?) {
        val url = baseUrl() + "/api/v1/enter-promo/" +
                ((promo).takeIf { promo != null } ?: "") +
                (("?clientId=$clientId").takeIf { clientId != null } ?: "")
        val restClient = RestClient.create()
        restClient.post().uri(url).retrieve()
    }

    fun getRandQuestion(
        clientId: String,
        levelId: String,
        ai: Boolean? = false,
    ): QuestionOutput {
        return getRandQuestionHttpRaw(clientId, levelId, ai).body(QuestionOutput::class.java)!!
    }

    fun getRandQuestionHttpRaw(
        clientId: String,
        levelId: String,
        ai: Boolean? = false,
    ): RestClient.ResponseSpec {
        return RestClient.create().get()
            .uri(baseUrl() + "/api/v1/questions/random?clientId=$clientId&levelId=$levelId&ai=$ai")
            .retrieve()
            .onStatus({it.is4xxClientError || it.is5xxServerError }) { request, response -> }
    }
}
