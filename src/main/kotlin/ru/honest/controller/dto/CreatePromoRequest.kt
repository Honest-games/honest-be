package ru.honest.controller.dto

import ru.honest.mybatis.model.enums.PromoType

data class CreatePromoRequest(
    val promoCode: String,
    val deckId: String,
    val promoType: PromoType
)