package ru.honest.controller

import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.QuestionModel
import ru.honest.service.GetRandQuestionAnswer

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

data class HonestError(
    val error: String,
)