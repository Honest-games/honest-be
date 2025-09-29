package ru.honest.service.question

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.model.enums.PromoType
import ru.honest.mybatis.repo.QuestionsHistoryRepo
import ru.honest.service.QuestionsService.GetQuestionAnswer
import ru.honest.service.dto.GenQuestionContext

@Component
@Order(1)
class FreeQuestionsLimitStrategy(
    private val questionsHistoryRepo: QuestionsHistoryRepo,
    @Value("\${honest.free-questions-limit:15}")
    private val freeQuestionsLimit: Int
) : GetQuestionStrategy {

    override fun shouldBeUsed(context: GenQuestionContext): Boolean {
        if (hasFullAccess(context)) {
            return false
        }

        val questionsCount = questionsHistoryRepo.countByClientIdAndDeck(context.clientId, context.deck.id)
        val limitExceeded = questionsCount >= freeQuestionsLimit

        if (limitExceeded) {
            log.info("[client ${context.clientId}] Free questions limit exceeded for deck ${context.deck.id}: $questionsCount >= $freeQuestionsLimit")
        }

        return limitExceeded
    }

    override fun getQuestion(context: GenQuestionContext): GetQuestionAnswer {
        return GetQuestionAnswer(
            question = QuestionModel(
                id = "-2",
                levelId = context.levelId,
                text = "Вы использовали все $freeQuestionsLimit бесплатных вопросов.",
                additionalText = "Для продолжения приобретите полный доступ к колоде."
            ),
            isLast = false
        )
    }

    private fun hasFullAccess(context: GenQuestionContext): Boolean {
        return context.clientEnteredPromos.any { promo ->
            promo.deckId == context.deck.id &&
            promo.promoType == PromoType.DECK_FULL_ACCESS.name
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FreeQuestionsLimitStrategy::class.java)
    }
}