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
        deck: DeckModel = decksFactory.createDeck(),
    ): LevelModel {
        val level = LevelModel(
            id = "$num",
            deckId = deck.id,
            order = num,
            name = "level $num",
            color = "0,0,0",
            description = "desc $num"
        )
        levelsRepo.save(level)
        return level
    }
}