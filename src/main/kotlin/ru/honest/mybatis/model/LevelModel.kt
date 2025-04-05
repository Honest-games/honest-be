package ru.honest.mybatis.model

data class LevelModel(
    val id: String,
    val deckId: String,
    val order: Int,
    val name: String,
    val color: String,
    val description: String,
)