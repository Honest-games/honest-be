package ru.honest.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.honest.exception.HonestEntityNotFound
import ru.honest.filterKeysNotNull
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.service.dto.GenQuestionContext
import ru.honest.service.question.GetQuestionStrategy

@Service
class QuestionsService(
    private val questionsRepo: QuestionsRepo,
    private val levelsRepo: LevelsRepo,
    private val getQuestionStrategies: List<GetQuestionStrategy>,
    private val decksRepo: DecksRepo,
    private val promoService: PromoService
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
        log.info("[$clientId] Getting question...")
        val context = collectGenQuestionContext(levelId, clientId, aiGen)
        getQuestionStrategies.forEach { getQuestionStrategy ->
            with(getQuestionStrategy) {
                if (shouldBeUsed(context)) {
                    log.info("[$clientId] Using strategy ${getQuestionStrategy::class.simpleName}")
                    val question = getQuestion(context)
                    log.info("[$clientId] Question got: ${question.question.text}")
                    return question
                }
            }
        }
        throw IllegalStateException("Question strategy not found for level $levelId and client $clientId")
    }

    fun collectGenQuestionContext(levelId: String, clientId: String, aiGen: Boolean): GenQuestionContext {
        val deck = decksRepo.getDecks(levelId = levelId).firstOrNull()
            ?: throw HonestEntityNotFound("Deck with id $levelId not found")
        val enteredPromos = promoService.getEnteredPromos(clientId)
        return GenQuestionContext(
            levelId = levelId,
            clientId = clientId,
            aiGen = aiGen,
            deck = deck,
            clientEnteredPromos = enteredPromos,
        )
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

    companion object {
        val log = LoggerFactory.getLogger(QuestionsService::class.java)
    }
}