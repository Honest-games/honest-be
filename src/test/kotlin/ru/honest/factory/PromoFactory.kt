package ru.honest.factory

import org.springframework.stereotype.Component
import ru.honest.mybatis.model.PromoModel
import ru.honest.mybatis.model.enums.PromoType
import ru.honest.mybatis.repo.PromosRepo

@Component
class PromoFactory(
    private val promosRepo: PromosRepo
) {
    fun createPromo(
        deckId: String,
        promoType: PromoType = PromoType.DECK_EXTEND_AI,
        promoCode: String = "promoCode"
    ): PromoModel? {
        val creating = PromoModel(
            id = -1,
            promoCode = promoCode,
            deckId = deckId,
            promoType = promoType,
            deleted = false
        )
        val id = promosRepo.createPromo(creating)
        return promosRepo.findPromoById(id)
    }
}