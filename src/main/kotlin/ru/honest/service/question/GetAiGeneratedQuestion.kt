package ru.honest.service.question

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.DecksRepo
import ru.honest.mybatis.repo.QuestionsRepo
import ru.honest.service.GenQuestionContext
import ru.honest.service.QuestionsService.GetQuestionAnswer
import ru.honest.service.gpt.GptService

@Component
class GetAiGeneratedQuestion(
    private val decksRepo: DecksRepo,
    private val questionsRepo: QuestionsRepo,
    private val gptService: GptService
) : GetQuestionStrategy {
    @Transactional
    override fun getQuestion(genQuestionContext: GenQuestionContext): GetQuestionAnswer {
        val clientId = genQuestionContext.clientId
        val levelId = genQuestionContext.levelId
        val levelQuestions = questionsRepo.getQuestionsByLevel(levelId)
        val alreadyGeneratedQuestions = questionsRepo.getAlreadyAiGeneratedQuestions(levelId, clientId)
        val similarQuestions = buildList {
            addAll(levelQuestions.map { it.text })
            addAll(alreadyGeneratedQuestions)
        }
        val aiGeneratedQuestion = gptService.createQuestionFromSimilar(similarQuestions)
        questionsRepo.addAiGeneratedQuestionToHistory(aiGeneratedQuestion, levelId, clientId)

        return GetQuestionAnswer(
            QuestionModel(
                id = "-1",
                levelId = levelId,
                text = aiGeneratedQuestion,
                additionalText = null
            ), false
        )
    }

    override fun shouldBeUsed(context: GenQuestionContext): Boolean {
        return context.deck.isAiOnly()
                || (context.aiGen
                && context.clientEnteredPromos.any { it.isDeckExtendAi() && it.deckId == context.deck.id }
                )
    }
}