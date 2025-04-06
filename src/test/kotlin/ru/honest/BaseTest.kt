package ru.honest

import org.flywaydb.core.Flyway
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

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
}
