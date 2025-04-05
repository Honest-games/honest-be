package ru.honest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.service.DecksService

@RestController
@RequestMapping("/api/v1/decks")
class DecksController(
    private val decksService: DecksService
) {
    @GetMapping
    fun getDecks(
        @RequestParam(required = true) clientId: String,
    ): List<DeckOutput> {
        val decksWithCounts = decksService.getDecksForMainPage(clientId)
        return decksWithCounts
    }
}
