package ru.honest.controller

import com.fasterxml.jackson.annotation.JsonInclude
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
    val bgImageId: String?,
    val modalImageId: String?,
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
                bgImageId = deck.bgImageId,
                modalImageId = deck.modalImageId,
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
    val counts: LevelCountsOutput,
    val backgroundColor: LevelBgColor
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
                counts = LevelCountsOutput(
                    questionsCount = questionsCount,
                    openedQuestionsCount = openedQuestionsCount,
                ),
                backgroundColor = LevelBgColor(
                    type = if (level.bgImageId != null) {
                        LevelBgColorType.BACKGROUND_IMAGE
                    } else {
                        LevelBgColorType.COLOR
                    },
                    color = level.color.takeIf { level.bgImageId == null },
                    imageId = level.bgImageId,
                )
            )
        }
    }
}

data class LevelBgColor(
    val type: LevelBgColorType,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val color: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val imageId: String? = null,
)

enum class LevelBgColorType {
    COLOR,
    BACKGROUND_IMAGE,
}

data class LevelCountsOutput(
    val questionsCount: Int,
    val openedQuestionsCount: Int,
)

data class HonestError(
    val error: String,
)