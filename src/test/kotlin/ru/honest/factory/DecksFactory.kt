package ru.honest.factory

import org.springframework.stereotype.Component
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.repo.DecksRepo

@Component
class DecksFactory(
    private val decksRepo: DecksRepo,
) : BaseFactory() {
    fun createDeck(
        id: String = num.toString(),
    ): DeckModel {
        val deck = DeckModel(
            id = id,
            name = "name$num",
            languageCode = "RU",
            description = "desc $num",
            labels = "$num;$num",
            imageId = "image$num",
            hidden = false,
            promo = null,
        )
        decksRepo.save(deck)
        return deck
    }

}