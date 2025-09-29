package ru.honest.service

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.honest.controller.dto.CreatePromoRequest
import ru.honest.controller.dto.PromoResponse
import ru.honest.exception.HonestEntityNotFound
import ru.honest.exception.InvalidPromoCodeException
import ru.honest.exception.PromoAlreadyExistsException
import ru.honest.mybatis.model.PromoModel
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.PromosRepo
import java.time.LocalDateTime

@Service
class PromoService(
    private val decksService: DecksService,
    private val promosRepo: PromosRepo,
    private val decksRepo: DecksRepo
) {
    fun enterPromo(
        clientId: String,
        promo: String,
    ) {
        val unlocked = decksService.tryUnlockDeck(clientId, promo)
        if(!unlocked) {
            promosRepo.findPromo(promo)?.let {
                promosRepo.enterPromo(it.id, clientId)
            }
        }
    }

    fun getEnteredPromos(clientId: String): Set<PromoModel> {
        return promosRepo.getEnteredPromos(clientId)
    }

    @Transactional
    fun createPromo(request: CreatePromoRequest): PromoResponse {
        // 1. Validate promo code format
        validatePromoCodeFormat(request.promoCode)

        // 2. Normalize promo code
        val normalizedCode = request.promoCode.uppercase().trim()

        // 3. Check for SQL injection/XSS
        validatePromoCodeSafety(normalizedCode)

        // 4. Check deck exists
        val deck = decksRepo.getDecks(id = request.deckId).firstOrNull()
            ?: throw HonestEntityNotFound("Deck with id '${request.deckId}' not found")

        // 5. Check for duplicate with retry logic for concurrent creation
        checkDuplicateWithRetry(normalizedCode)

        // 6. Create promo
        val promoModel = PromoModel(
            id = 0, // Will be set by DB
            promoCode = normalizedCode,
            deckId = request.deckId,
            promoType = request.promoType,
            deleted = false
        )

        val promoId = try {
            promosRepo.createPromo(promoModel)
        } catch (e: DuplicateKeyException) {
            throw PromoAlreadyExistsException("Promo code '$normalizedCode' already exists")
        }

        return PromoResponse(
            id = promoId,
            promoCode = normalizedCode,
            deckId = request.deckId,
            promoType = request.promoType,
            createdAt = LocalDateTime.now()
        )
    }

    private fun validatePromoCodeFormat(code: String) {
        if (code.isBlank()) {
            throw InvalidPromoCodeException("Promo code cannot be empty")
        }

        if (code.length < 6 || code.length > 20) {
            throw InvalidPromoCodeException("Promo code must be 6-20 characters long")
        }

        val promoCodeRegex = "^[A-Z0-9]{6,20}$".toRegex()
        if (!code.uppercase().matches(promoCodeRegex)) {
            throw InvalidPromoCodeException("Promo code must contain only letters and numbers")
        }
    }

    private fun validatePromoCodeSafety(code: String) {
        val dangerousPatterns = listOf(
            "SELECT", "INSERT", "UPDATE", "DELETE", "DROP",
            "<script", "javascript:", "onclick", "onerror"
        )

        val upperCode = code.uppercase()
        if (dangerousPatterns.any { upperCode.contains(it) }) {
            throw InvalidPromoCodeException("Promo code contains invalid characters")
        }
    }

    private fun checkDuplicateWithRetry(promoCode: String) {
        val maxRetries = 3
        repeat(maxRetries) { attempt ->
            val existing = promosRepo.findPromo(promoCode)
            if (existing != null) {
                throw PromoAlreadyExistsException("Promo code '$promoCode' already exists")
            }

            // Small delay for retry on race condition
            if (attempt < maxRetries - 1) {
                Thread.sleep(50L * (attempt + 1))
            }
        }
    }
}