package ru.honest.factory

import org.springframework.stereotype.Component
import ru.honest.mybatis.model.DeckAiType
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
        order: Int = 1,
        aiType: DeckAiType = DeckAiType.NON_AI,
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
            bgImageId = null,
            modalImageId = null,
            order = order,
            aiType = aiType,
            color = "0,0,0",
        )
        decksRepo.save(deck)
        return deck
    }

    fun createDeck(deck: DeckModel): DeckModel {
        decksRepo.save(deck)
        return deck
    }
}