package ru.honest.mybatis.model

data class DeckModel(
    val id: String,
    val name: String,
    val languageCode: String,
    val description: String?,
    val labels: String?,
    val imageId: String,
    val hidden: Boolean,
    val promo: String?,
    val bgImageId: String?,
    val modalImageId: String?,
    val order: Int,
    val aiType: DeckAiType,
){
    fun isAiOnly() = aiType == DeckAiType.AI_ONLY
    fun isNonAi() = aiType == DeckAiType.NON_AI
}