package ru.honest.controller

import ru.honest.mybatis.model.DeckModel

data class DeckOutput(
    val id: String,
    val languageCode: String,
    val name: String,
    val description: String?,
    val labels: List<String>,
    val imageId: String,
    val cardsCount: Int,
    val openedCount: Int,
){
    companion object {
        fun create(deck: DeckModel, cardsCount: Int, openedCount: Int): DeckOutput {
            return DeckOutput(
                id = deck.id,
                languageCode = deck.languageCode,
                name = deck.name,
                description = deck.description,
                labels = deck.labels?.split(";") ?: emptyList(),
                imageId = deck.imageId,
                cardsCount = cardsCount,
                openedCount = openedCount
            )
        }
    }
}