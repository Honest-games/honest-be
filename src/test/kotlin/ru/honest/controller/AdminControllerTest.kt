package ru.honest.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import ru.honest.BaseTest
import ru.honest.controller.dto.CreatePromoRequest
import ru.honest.controller.dto.PromoResponse
import ru.honest.factory.DecksFactory
import ru.honest.mybatis.model.enums.PromoType
import ru.honest.mybatis.repo.PromosRepo

@TestConstructor(autowireMode = ALL)
class AdminControllerTest(
    private val decksFactory: DecksFactory,
    private val promosRepo: PromosRepo,
) : BaseTest() {

    @Test
    fun `create promo - success`() {
        val deck = decksFactory.createDeck()
        val request = CreatePromoRequest(
            promoCode = "SUMMER2024",
            deckId = deck.id,
            promoType = PromoType.DECK_FULL_ACCESS
        )

        val response = createPromo(request)

        assertEquals("SUMMER2024", response.promoCode)
        assertEquals(deck.id, response.deckId)
        assertEquals(PromoType.DECK_FULL_ACCESS, response.promoType)
        assertTrue(response.id > 0)
        assertNotNull(response.createdAt)

        // Verify in database
        val savedPromo = promosRepo.findPromo("SUMMER2024")
        assertNotNull(savedPromo)
        assertEquals("SUMMER2024", savedPromo!!.promoCode)
        assertEquals(deck.id, savedPromo.deckId)
    }

    @Test
    fun `create promo - duplicate code fails with 409`() {
        val deck = decksFactory.createDeck()
        val request = CreatePromoRequest(
            promoCode = "DUPLICATE123",
            deckId = deck.id,
            promoType = PromoType.DECK_EXTEND_AI
        )

        // First creation should succeed
        createPromo(request)

        // Second creation should fail
        val exception = assertThrows(HttpClientErrorException.Conflict::class.java) {
            createPromo(request)
        }
        assertTrue(exception.responseBodyAsString.contains("already exists"))
    }

    @Test
    fun `create promo - non-existent deck fails with 404`() {
        val request = CreatePromoRequest(
            promoCode = "VALIDCODE123",
            deckId = "non-existent-deck",
            promoType = PromoType.DECK_FULL_ACCESS
        )

        val exception = assertThrows(HttpClientErrorException.NotFound::class.java) {
            createPromo(request)
        }
        assertTrue(exception.responseBodyAsString.contains("Deck with id"))
        assertTrue(exception.responseBodyAsString.contains("not found"))
    }

    @Test
    fun `create promo - invalid format fails with 400`() {
        val deck = decksFactory.createDeck()

        // Test cases for invalid formats
        val invalidCodes = listOf(
            "SHORT",      // Too short (< 6 chars)
            "VERY_LONG_PROMO_CODE_OVER_TWENTY", // Too long (> 20 chars)
            "INVALID-CHARS", // Contains hyphen
            "invalid@code",  // Contains special chars
            "",              // Empty
            "   ",           // Only spaces
            "lower123",      // Will be normalized, but test mixed case
            "SELECT123"      // Contains SQL keyword
        )

        invalidCodes.forEach { invalidCode ->
            val request = CreatePromoRequest(
                promoCode = invalidCode,
                deckId = deck.id,
                promoType = PromoType.DECK_FULL_ACCESS
            )

            assertThrows(HttpClientErrorException.BadRequest::class.java,
                "Code '$invalidCode' should be invalid") {
                createPromo(request)
            }
        }
    }

    @Test
    fun `create promo - normalizes case and trims spaces`() {
        val deck = decksFactory.createDeck()
        val request = CreatePromoRequest(
            promoCode = "  lowercase123  ",
            deckId = deck.id,
            promoType = PromoType.DECK_EXTEND_AI
        )

        val response = createPromo(request)

        assertEquals("LOWERCASE123", response.promoCode)

        // Verify in database
        val savedPromo = promosRepo.findPromo("LOWERCASE123")
        assertNotNull(savedPromo)
        assertEquals("LOWERCASE123", savedPromo!!.promoCode)
    }

    @Test
    fun `create promo - validates XSS and injection attempts`() {
        val deck = decksFactory.createDeck()

        val dangerousCodes = listOf(
            "<script>alert('xss')</script>",
            "javascript:alert(1)",
            "SELECT * FROM users",
            "INSERT INTO promos",
            "onclick=alert(1)"
        )

        dangerousCodes.forEach { dangerousCode ->
            val request = CreatePromoRequest(
                promoCode = dangerousCode,
                deckId = deck.id,
                promoType = PromoType.DECK_FULL_ACCESS
            )

            assertThrows(HttpClientErrorException.BadRequest::class.java,
                "Dangerous code '$dangerousCode' should be rejected") {
                createPromo(request)
            }
        }
    }

    @Test
    fun `create promo - edge case valid formats`() {
        val deck = decksFactory.createDeck()

        val validCodes = listOf(
            "123456",        // Numbers only, minimum length
            "ABCDEFGHIJKLMNOPQRST", // Letters only, maximum length (20 chars)
            "MIX123ABC",     // Mixed letters and numbers
            "000000"         // All zeros
        )

        validCodes.forEach { validCode ->
            val request = CreatePromoRequest(
                promoCode = validCode,
                deckId = deck.id,
                promoType = PromoType.DECK_EXTEND_AI
            )

            val response = createPromo(request)
            assertEquals(validCode.uppercase(), response.promoCode)
        }
    }

    @Test
    fun `create promo - different promo types work correctly`() {
        val deck = decksFactory.createDeck()

        // Test DECK_EXTEND_AI
        val extendAiRequest = CreatePromoRequest(
            promoCode = "EXTEND123",
            deckId = deck.id,
            promoType = PromoType.DECK_EXTEND_AI
        )
        val extendAiResponse = createPromo(extendAiRequest)
        assertEquals(PromoType.DECK_EXTEND_AI, extendAiResponse.promoType)

        // Test DECK_FULL_ACCESS
        val fullAccessRequest = CreatePromoRequest(
            promoCode = "FULLACCESS123",
            deckId = deck.id,
            promoType = PromoType.DECK_FULL_ACCESS
        )
        val fullAccessResponse = createPromo(fullAccessRequest)
        assertEquals(PromoType.DECK_FULL_ACCESS, fullAccessResponse.promoType)
    }

    private fun createPromo(request: CreatePromoRequest): PromoResponse {
        return RestClient.create().post()
            .uri(baseUrl() + "/api/v1/admin/promos")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<PromoResponse>() {})!!
    }
}