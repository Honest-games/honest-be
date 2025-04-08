package ru.honest.factory

import org.springframework.stereotype.Component
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.repo.DecksRepo

@Component
class DecksFactory(
    private val decksRepo: DecksRepo,
) : BaseFactory() {
    fun createDeck(
        id: String? = null,
        hidden: Boolean = false,
        promo: String? = null,
    ): DeckModel {
        val deck = DeckModel(
            id = id ?: num.toString(),
            name = "name$num",
            languageCode = "RU",
            description = "desc $num",
            labels = "$num;$num",
            imageId = "image$num",
            hidden = hidden,
            promo = promo,
        )
        decksRepo.save(deck)
        return deck
    }

    fun unlockDeck(clientId: String, deckId: String) = decksRepo.unlockDeck(clientId, deckId)
}