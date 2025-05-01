package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.PromoModel
import ru.honest.mybatis.model.enums.PromoType

@Mapper
interface PromosRepo {
    fun findPromo(
        promoCode: String,
    ): PromoModel?

    fun findPromoById(
        id: Int,
    ): PromoModel?

    fun enterPromo(
        promoId: Int,
        clientId: String,
    )

    fun createPromo(
        promo: PromoModel,
    ): Int

    fun getEnteredPromos(
        clientId: String,
        promoType: PromoType? = null
    ): Set<PromoModel>
}