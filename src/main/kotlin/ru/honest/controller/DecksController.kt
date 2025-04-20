package ru.honest.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.honest.service.DecksService
import ru.honest.service.gpt.ChatMessage
import ru.honest.service.gpt.GptChatRole
import ru.honest.service.gpt.GptClient

@RestController
@RequestMapping("/api/v1/decks")
class DecksController(
    private val decksService: DecksService,
    private val gptClient: GptClient,
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

    @GetMapping("/ai")
    fun ai(
        @RequestParam model: String,
        @RequestParam prompt: String,
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(gptClient.chatCompletion(
            model,
            listOf(ChatMessage(GptChatRole.USER, prompt))
        ))
    }
}
