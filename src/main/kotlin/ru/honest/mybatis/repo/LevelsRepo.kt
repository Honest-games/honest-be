package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.LevelModel

@Mapper
interface LevelsRepo {
    fun getLevelsByDeck(deckId: String): List<LevelModel>
    fun save(level: LevelModel)
    fun exists(levelId: String): Boolean
}

