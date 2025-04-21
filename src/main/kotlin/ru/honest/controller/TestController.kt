package ru.honest.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.service.gpt.ChatMessage
import ru.honest.service.gpt.GptChatRole
import ru.honest.service.gpt.GptClient

@RestController
@RequestMapping("/test")
@Profile("test.controller")
class TestController(
    private val gptClient: GptClient
) {
    @GetMapping("/ai")
    fun ai(
        @RequestParam model: String,
        @RequestParam prompt: String,
    ): Any {
        return ResponseEntity.ok(
            gptClient.chatCompletion(
                listOf(ChatMessage(GptChatRole.USER, prompt))
            )
        )
    }
}