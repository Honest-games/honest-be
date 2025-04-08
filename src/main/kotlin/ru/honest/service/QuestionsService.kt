package ru.honest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.honest.config.HonestProps
import ru.honest.exception.HonestEntityNotFound
import ru.honest.filterKeysNotNull
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.model.QuestionHistoryModel
import ru.honest.mybatis.model.UsedQuestion
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.QuestionsHistoryRepo
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.mybatis.repo.UsedQuestionsRepo
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionsService(
    private val questionsRepo: QuestionsRepo,
    private val usedQuestionsRepo: UsedQuestionsRepo,
    private val questionsHistoryRepo: QuestionsHistoryRepo,
    private val honestProps: HonestProps,
    private val levelsRepo: LevelsRepo
) {
    @Transactional
    fun readRandomQuestion(
        levelId: String,
        clientId: String,
    ): GetRandQuestionAnswer {
        if(!levelsRepo.exists(levelId)) {
            throw HonestEntityNotFound("Level $levelId not found")
        }
        val usedLevelQuestions = usedQuestionsRepo.getUsedQuestions(clientId, setOf(levelId))
        val questions = questionsRepo.getQuestionsByLevel(levelId)
        val notUsedQuestions = (
                questions.associateBy { it.id } - usedLevelQuestions.map { it.questionId }.toSet())
            .values
        if (notUsedQuestions.isEmpty()) {
            usedQuestionsRepo.clearUsedQuestions(setOf(levelId), clientId)
            return GetRandQuestionAnswer(QuestionModel(
                id = "-1",
                levelId = levelId,
                text = honestProps.lastCardText,
                additionalTest = null
            ), true)
        }
        val question = notUsedQuestions.random()
        usedQuestionsRepo.save(UsedQuestion(question.id, clientId))
        questionsHistoryRepo.save(QuestionHistoryModel(
            id = UUID.randomUUID().toString(),
            levelId = levelId,
            questionId = question.id,
            clientId = clientId,
            time = LocalDateTime.now()
        ))
        return GetRandQuestionAnswer(question, false)
    }

    fun getQuestionsByLevel(levels: List<LevelModel>): Map<LevelModel, List<QuestionModel>>{
        val questions = questionsRepo.getByParams(levels.map { it.id })
        return questions.groupBy { q ->
            levels.find { q.levelId == it.id }
        }.filterKeysNotNull()
    }

    data class GetRandQuestionAnswer(
        val question: QuestionModel,
        val isLast: Boolean,
    )
}