package ru.honest.controller

import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.model.QuestionModel

data class DeckOutput(
    val id: String,
    val languageCode: String,
    val name: String,
    val description: String?,
    val labels: List<String>,
    val imageId: String,
){
    companion object {
        fun create(deck: DeckModel): DeckOutput {
            return DeckOutput(
                id = deck.id,
                languageCode = deck.languageCode,
                name = deck.name,
                description = deck.description,
                labels = deck.labels?.split(";") ?: emptyList(),
                imageId = deck.imageId,
            )
        }
    }
}

data class QuestionOutput(
    val id: String,
    val levelId: String,
    val text: String,
    val additionalTest: String?,
    val isLast: Boolean,
){
    companion object {
        fun create(q: QuestionModel, isLast: Boolean): QuestionOutput {
            return QuestionOutput(
                id = q.id,
                levelId = q.levelId,
                text = q.text,
                additionalTest = q.additionalTest,
                isLast = isLast
            )
        }
    }
}

data class LevelOutput(
    val id: String,
    val deckId: String,
    val order: Int,
    val name: String,
    val description: String?,
    val color: String,
    val counts: LevelCountsOutput,
){
    companion object {
        fun create(
            level: LevelModel,
            questionsCount: Int,
            openedQuestionsCount: Int
        ): LevelOutput {
            return LevelOutput(
                id = level.id,
                deckId = level.deckId,
                order = level.order,
                name = level.name,
                description = level.description,
                color = level.color,
                counts = LevelCountsOutput(
                    questionsCount = questionsCount,
                    openedQuestionsCount = openedQuestionsCount,
                )
            )
        }
    }
}

data class LevelCountsOutput(
    val questionsCount: Int,
    val openedQuestionsCount: Int,
)

data class HonestError(
    val error: String,
)