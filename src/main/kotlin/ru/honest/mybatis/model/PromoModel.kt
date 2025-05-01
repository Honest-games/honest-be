package ru.honest.mybatis.model

import ru.honest.mybatis.model.enums.PromoType

data class PromoModel(
    val id: Int,
    val promoCode: String,
    val deckId: String,
    val promoType: PromoType,
    val deleted: Boolean,
) {
    fun isDeckExtendAi() = promoType == PromoType.DECK_EXTEND_AI
}
