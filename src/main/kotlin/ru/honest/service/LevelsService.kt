package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.LevelOutput
import ru.honest.mybatis.repo.LevelsRepo

@Service
class LevelsService(
    private val levelsRepo: LevelsRepo,
    private val usedQuestionsService: UsedQuestionsService,
    private val questionsService: QuestionsService,
) {
    fun getLevelsForMainPage(clientId: String, deckId: String): List<LevelOutput> {
        val levels = levelsRepo.getLevelsByDeck(deckId)
        val openedCardsByLevel = usedQuestionsService.getOpenedCardsByLevel(clientId, levels.map { it.id })
        val questionsByLevel = questionsService.getQuestionsByLevel(levels)
        return levels.map {
            LevelOutput.create(
                it,
                questionsByLevel[it]?.size ?: 0,
                openedQuestionsCount = openedCardsByLevel[it.id]?.size ?: 0
            )
        }
    }
}