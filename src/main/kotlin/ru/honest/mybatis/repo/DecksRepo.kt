package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.DeckCardsCount

@Mapper
interface DecksRepo {
    fun getDecks(
        promo: String? = null,
    ): List<DeckModel>
    fun getCardsCounts(decksIds: List<String>): List<DeckCardsCount> = listOf()
    fun getUnlockedDecksIds(clientId: String): List<String>
    fun save(deck: DeckModel)
    fun unlockDeck(clientId: String, deckId: String)
}

