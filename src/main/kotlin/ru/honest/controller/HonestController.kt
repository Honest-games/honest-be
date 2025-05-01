package ru.honest.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.honest.service.PromoService

@Tag(name = "default")
@RestController
@RequestMapping("/api/v1/")
class HonestController(
    private val promoService: PromoService,
) {
    @PostMapping("/enter-promo/{promo}")
    fun enterPromo(
        @PathVariable promo: String,
        @RequestParam clientId: String,
    ): ResponseEntity<Any> {
        promoService.enterPromo(clientId, promo)
        return ResponseEntity.ok().build()
    }
}
