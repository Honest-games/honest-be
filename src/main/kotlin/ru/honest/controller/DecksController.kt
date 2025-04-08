package ru.honest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.service.DecksService

@Tag(name = "default")
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
}
