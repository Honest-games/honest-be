package ru.honest.service.question

import org.springframework.stereotype.Component
import ru.honest.config.HonestProps
import ru.honest.mybatis.model.QuestionHistoryModel
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.model.UsedQuestion
import ru.honest.mybatis.repo.*
import ru.honest.service.QuestionsService.GetQuestionAnswer
import java.time.LocalDateTime
import java.util.*

@Component
class GetRandomQuestionStrategy(
    private val levelsRepo: LevelsRepo,
    private val usedQuestionsRepo: UsedQuestionsRepo,
    private val questionsRepo: QuestionsRepo,
    private val honestProps: HonestProps,
    private val questionsHistoryRepo: QuestionsHistoryRepo,
    private val decksRepo: DecksRepo
) : GetQuestionStrategy {
    override fun getQuestion(levelId: String, clientId: String, ai: Boolean): GetQuestionAnswer {
        val usedLevelQuestions = usedQuestionsRepo.getUsedQuestions(clientId, setOf(levelId))
        val questions = questionsRepo.getQuestionsByLevel(levelId)
        val notUsedQuestions = (
                questions.associateBy { it.id } - usedLevelQuestions.map { it.questionId }.toSet())
            .values
        if (notUsedQuestions.isEmpty()) {
            usedQuestionsRepo.clearUsedQuestions(setOf(levelId), clientId)
            return GetQuestionAnswer(
                QuestionModel(
                    id = "-1",
                    levelId = levelId,
                    text = honestProps.lastCardText,
                    additionalText = null
                ), true
            )
        }
        val question = notUsedQuestions.random()
        usedQuestionsRepo.save(UsedQuestion(question.id, clientId))
        questionsHistoryRepo.save(
            QuestionHistoryModel(
                id = UUID.randomUUID().toString(),
                levelId = levelId,
                questionId = question.id,
                clientId = clientId,
                time = LocalDateTime.now()
            )
        )
        return GetQuestionAnswer(question, false)
    }

    override fun shouldBeUsed(levelId: String, clientId: String, ai: Boolean): Boolean {
        val deck = decksRepo.getDecks(levelId = levelId).firstOrNull() ?: return false
        return deck.isNonAi() || (deck.isAiExtended() && !ai)
    }
}