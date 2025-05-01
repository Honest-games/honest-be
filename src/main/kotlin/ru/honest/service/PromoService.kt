package ru.honest.service

import org.springframework.stereotype.Service
import ru.honest.mybatis.model.PromoModel
import ru.honest.mybatis.repo.PromosRepo

@Service
class PromoService(private val decksService: DecksService, private val promosRepo: PromosRepo) {
    fun enterPromo(
        clientId: String,
        promo: String,
    ) {
        val unlocked = decksService.tryUnlockDeck(clientId, promo)
        if(!unlocked) {
            promosRepo.findPromo(promo)?.let {
                promosRepo.enterPromo(it.id, clientId)
            }
        }
    }

    fun getEnteredPromos(clientId: String): Set<PromoModel> {
        return promosRepo.getEnteredPromos(clientId)
    }
}