package ru.honest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.service.LevelsService

@RestController
@RequestMapping("/api/v1/levels")
class LevelsController(
    private val levelsService: LevelsService,
) {
    @GetMapping
    fun getLevels(
        @RequestParam clientId: String,
        @RequestParam deckId: String,
    ): List<LevelOutput> {
        val levelsByDeck = levelsService.getLevelsForMainPage(clientId, deckId)
        return levelsByDeck
    }
}