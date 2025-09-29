package ru.honest.controller.dto

import ru.honest.mybatis.model.enums.PromoType
import java.time.LocalDateTime

data class PromoResponse(
    val id: Int,
    val promoCode: String,
    val deckId: String,
    val promoType: PromoType,
    val createdAt: LocalDateTime
)