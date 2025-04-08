package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.DeckOutput
import ru.honest.mybatis.repo.DecksRepo

@Service
class DecksService(
    private val decksRepo: DecksRepo,
) {
    fun getDecksForMainPage(clientId: String): List<DeckOutput> {
        val unlockedDecks = decksRepo.getUnlockedDecksIds(clientId).toSet()
        val decksToReturn = decksRepo.getDecks()
            .filter { !it.hidden || unlockedDecks.contains(it.id) }

        return decksToReturn.map { DeckOutput.create(it) }
    }

    fun tryUnlockDeck(clientId: String, promo: String) =
        decksRepo.getDecks(promo = promo).firstOrNull()?.let { decksRepo.unlockDeck(clientId, it.id) }

}

