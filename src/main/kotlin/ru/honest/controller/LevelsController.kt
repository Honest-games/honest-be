package ru.honest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.repo.LevelsRepo

@RestController
@RequestMapping("/api/v1/levels")
class LevelsController(
    private val levelsRepo: LevelsRepo,
) {

    @GetMapping
    fun getLevels(
        @RequestParam(required = true) deckId: String,
    ): List<LevelModel> {
        return levelsRepo.getLevelsByDeck(deckId)
    }
}