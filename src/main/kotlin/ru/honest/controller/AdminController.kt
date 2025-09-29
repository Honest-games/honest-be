package ru.honest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.honest.controller.dto.CreatePromoRequest
import ru.honest.controller.dto.PromoResponse
import ru.honest.service.PromoService

@Tag(name = "Admin", description = "Admin operations for managing promo codes")
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val promoService: PromoService
) {
    @Operation(
        summary = "Create promo code",
        description = "Creates a new promo code for deck access. Promo code must be 6-20 alphanumeric characters."
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Promo created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request data or promo code format"),
        ApiResponse(responseCode = "404", description = "Deck not found"),
        ApiResponse(responseCode = "409", description = "Promo code already exists")
    )
    @PostMapping("/promos")
    fun createPromo(
        @RequestBody request: CreatePromoRequest
    ): ResponseEntity<PromoResponse> {
        val promo = promoService.createPromo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(promo)
    }
}