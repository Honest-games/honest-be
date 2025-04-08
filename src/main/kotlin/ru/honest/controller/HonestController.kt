package ru.honest.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.honest.service.DecksService

@Tag(name = "default")
@RestController
@RequestMapping("/api/v1/")
class HonestController(
    private val decksService: DecksService
) {
    @PostMapping("/enter-promo/{promo}")
    fun enterPromo(
        @PathVariable promo: String,
        @RequestParam clientId: String,
    ): ResponseEntity<Any> {
        decksService.tryUnlockDeck(clientId, promo)
        return ResponseEntity.ok().build()
    }
}
