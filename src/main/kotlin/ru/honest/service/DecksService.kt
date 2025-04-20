package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.DeckOutput
import ru.honest.exception.HonestEntityNotFound
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.LevelsRepo

@Service
class DecksService(
    private val decksRepo: DecksRepo,
    private val usedQuestionsService: UsedQuestionsService,
    private val levelsRepo: LevelsRepo,
) {
    fun getDecksForMainPage(clientId: String): List<DeckOutput> {
        val unlockedDecks = decksRepo.getUnlockedDecksIds(clientId).toSet()
        val decksToReturn = decksRepo.getDecks()
            .filter { !it.hidden || unlockedDecks.contains(it.id) }
            .sortedBy { it.order }

        return decksToReturn.map { DeckOutput.create(it) }
    }

    fun tryUnlockDeck(clientId: String, promo: String) =
        decksRepo.getDecks(promo = promo).firstOrNull()?.let { decksRepo.unlockDeck(clientId, it.id) }

    fun shuffleDeck(clientId: String, deckId: String) {
        val deck = decksRepo.getDecks(id = deckId).firstOrNull() ?: throw HonestEntityNotFound(
            "Deck $deckId not found"
        )
        val levels = levelsRepo.getLevelsByDeck(deckId = deck.id)

        usedQuestionsService.clearUsedQuestions(clientId, levels.map { it.id })
    }
}

