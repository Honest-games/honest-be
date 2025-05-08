package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.DeckOutput
import ru.honest.exception.HonestEntityNotFound
import ru.honest.mybatis.model.DeckAiType
import ru.honest.mybatis.model.enums.PromoType
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.PromosRepo

@Service
class DecksService(
    private val decksRepo: DecksRepo,
    private val usedQuestionsService: UsedQuestionsService,
    private val levelsRepo: LevelsRepo,
    private val promosRepo: PromosRepo,
) {
    fun getDecksForMainPage(clientId: String): List<DeckOutput> {
        val unlockedDecks = decksRepo.getUnlockedDecksIds(clientId).toSet()
        val aiUnlockedDecksIds = promosRepo.getEnteredPromos(clientId, promoType = PromoType.DECK_EXTEND_AI)
            .map { it.deckId }.toSet()

        val decksToReturn = decksRepo.getDecks()
            .filter { !it.hidden || unlockedDecks.contains(it.id) }
            .sortedBy { it.order }

        return decksToReturn.map { DeckOutput.create(it, when (it.aiType) {
            DeckAiType.NON_AI ->
                if(aiUnlockedDecksIds.contains(it.id)) DeckAiType.AI_EXTENDED
                else DeckAiType.NON_AI
            else -> it.aiType
        }) }
    }

    fun tryUnlockDeck(clientId: String, promo: String): Boolean {
        val foundDeck = decksRepo.getDecks(promo = promo).firstOrNull()
        foundDeck?.let { decksRepo.unlockDeck(clientId, it.id) }
        return foundDeck != null
    }

    fun shuffleDeck(clientId: String, deckId: String) {
        val deck = decksRepo.getDecks(id = deckId).firstOrNull() ?: throw HonestEntityNotFound(
            "Deck $deckId not found"
        )
        val levels = levelsRepo.getLevelsByDeck(deckId = deck.id)

        usedQuestionsService.clearUsedQuestions(clientId, levels.map { it.id })
    }
}

