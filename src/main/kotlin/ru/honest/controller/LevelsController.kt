package ru.honest.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/{id}/shuffle")
    fun shuffleLevel(
        @RequestParam clientId: String,
        @PathVariable id: String,
    ): ResponseEntity<Any> {
        levelsService.shuffleLevel(clientId, id)
        return ResponseEntity.ok().build()
    }
}