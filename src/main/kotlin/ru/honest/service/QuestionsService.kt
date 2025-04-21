package ru.honest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.honest.config.HonestProps
import ru.honest.exception.HonestEntityNotFound
import ru.honest.filterKeysNotNull
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.QuestionsHistoryRepo
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.mybatis.repo.UsedQuestionsRepo
import ru.honest.service.question.GetQuestionStrategy

@Service
class QuestionsService(
    private val questionsRepo: QuestionsRepo,
    private val usedQuestionsRepo: UsedQuestionsRepo,
    private val questionsHistoryRepo: QuestionsHistoryRepo,
    private val honestProps: HonestProps,
    private val levelsRepo: LevelsRepo,
    private val getQuestionStrategies: List<GetQuestionStrategy>
) {
    @Transactional
    fun getQuestion(
        levelId: String,
        clientId: String,
        aiGen: Boolean = false,
    ): GetQuestionAnswer {
        if (!levelsRepo.exists(levelId)) {
            throw HonestEntityNotFound("Level $levelId not found")
        }
        getQuestionStrategies.forEach { getQuestionStrategy ->
            with(getQuestionStrategy){
                if (shouldBeUsed(levelId, clientId, aiGen)) {
                    return getQuestion(levelId, clientId, aiGen)
                }
            }
        }
        throw IllegalStateException("Question strategy not found for level $levelId and client $clientId")
    }

    fun getQuestionsByLevel(levels: List<LevelModel>): Map<LevelModel, List<QuestionModel>>{
        val questions = questionsRepo.getByParams(levels.map { it.id })
        return questions.groupBy { q ->
            levels.find { q.levelId == it.id }
        }.filterKeysNotNull()
    }

    data class GetQuestionAnswer(
        val question: QuestionModel,
        val isLast: Boolean,
    )
}