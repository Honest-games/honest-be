package ru.honest.factory

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.repo.LevelsRepo

@Component
class LevelsFactory(
    private val levelsRepo: LevelsRepo,
    @Lazy
    private val decksFactory: DecksFactory,
): BaseFactory() {
    fun createLevel(
        deck: DeckModel? = null,
        id: String = num.toString(),
        bgImageId: String? = null,
    ): LevelModel {
        val createdDeck = deck ?: decksFactory.createDeck()
        val level = LevelModel(
            id = id,
            deckId = createdDeck.id,
            order = num,
            name = "level $num",
            color = "0,0,0",
            description = "desc $num",
            bgImageId = bgImageId,
        )
        levelsRepo.save(level)
        return level
    }

    fun createLevel(level: LevelModel): LevelModel {
        levelsRepo.save(level)
        return level
    }
}