package ru.honest.service.dto

import ru.honest.mybatis.model.DeckAiType
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.PromoModel

data class GenQuestionContext(
    val levelId: String,
    val clientId: String,
    val aiGen: Boolean,
    val deckAiType: DeckAiType? = null,
    val deck: DeckModel,
    val clientEnteredPromos: Set<PromoModel>,
)