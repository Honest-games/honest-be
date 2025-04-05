package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.DeckCardsCount

@Mapper
interface DecksRepo {
    fun getDecks(): List<DeckModel>
    fun getCardsCounts(decksIds: List<String>): List<DeckCardsCount> = listOf()
//    fun getOpenedCounts(decksIds: List<String>, clientId: String): List<DeckCardsCount> = listOf()
    fun save(deck: DeckModel)
}

