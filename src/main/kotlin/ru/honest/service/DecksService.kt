package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.DeckOutput
import ru.honest.mybatis.repo.DecksRepo

@Service
class DecksService(
    private val decksRepo: DecksRepo,
) {
    fun getDecksForMainPage(clientId: String): List<DeckOutput> {
        val decks = decksRepo.getDecks()
        val decksIds = decks.map { it.id }

        return decks.map { DeckOutput.create(it) }
    }
}

