package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.filterKeysNotNull
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.mybatis.repo.UsedQuestionsRepo

@Service
class UsedQuestionsService(
    private val usedQuestionsRepo: UsedQuestionsRepo,
    private val questionsRepo: QuestionsRepo,
){
    fun getOpenedCardsByLevel(clientId: String, levelsIds: List<String>): Map<String, List<QuestionModel>> {
        val questionsById = questionsRepo.getByParams(levelsIds).associateBy { it.id }
        val usedQuestions = usedQuestionsRepo.getUsedQuestions(clientId, levelsIds)
        return usedQuestions.groupBy({ questionsById[it.questionId]?.levelId }) {
            questionsById[it.questionId]
        }.filterKeysNotNull().mapValues { it.value.filterNotNull() }
    }
}