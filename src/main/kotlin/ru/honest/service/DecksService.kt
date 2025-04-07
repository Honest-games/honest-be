package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.controller.DeckOutput
import ru.honest.mapKeysNotNull
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.mybatis.repo.UsedQuestionsRepo

@Service
class DecksService(
    private val decksRepo: DecksRepo,
    private val levelsRepo: LevelsRepo,
    private val usedQuestionsRepo: UsedQuestionsRepo,
    private val questionsRepo: QuestionsRepo,
) {
    fun getDecksForMainPage(clientId: String): List<DeckOutput> {
        val decks = decksRepo.getDecks()
        val decksIds = decks.map { it.id }
        val cardsCounts = decksRepo.getCardsCounts(decksIds).associateBy { it.deckId }

        return decks.map { DeckOutput.create(it) }
    }

    private fun getOpenedCardsByLevel(clientId: String, levelsIds: List<String>): Map<String, List<QuestionModel>> {
        val questionsById = questionsRepo.getByParams(levelsIds).associateBy { it.id }
        val usedQuestions = usedQuestionsRepo.getUsedQuestions(clientId, levelsIds)
        return usedQuestions.groupBy({ questionsById[it.questionId]?.levelId }) {
            questionsById[it.questionId]
        }.mapKeysNotNull().mapValues { it.value.filterNotNull() }
    }
}

