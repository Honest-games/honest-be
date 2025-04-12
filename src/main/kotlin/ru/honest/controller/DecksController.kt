package ru.honest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.honest.service.DecksService

@RestController
@RequestMapping("/api/v1/decks")
class DecksController(
    private val decksService: DecksService
) {
    @GetMapping
    @Operation(summary = "Get all decks (WITHOUT LANGUAGE PARAM NOW)")
    fun getDecks(
        @RequestParam clientId: String,
    ): List<DeckOutput> {
        val decks = decksService.getDecksForMainPage(clientId)
        return decks
    }

    @PostMapping("/{id}/shuffle")
    fun shuffleLevel(
        @RequestParam clientId: String,
        @PathVariable id: String,
    ): ResponseEntity<Any> {
        decksService.shuffleDeck(clientId, id)
        return ResponseEntity.ok().build()
    }
}
