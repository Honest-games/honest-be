package ru.honest.repo

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.mybatis.repo.LevelsRepo
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class LevelsRepoTest(
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val levelsRepo: LevelsRepo,
): BaseTest() {
    @Test
    fun `gets levels by deckId`() {
        val deck = decksFactory.createDeck()
        val deck2 = decksFactory.createDeck()
        val levels = listOf(
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
            levelsFactory.createLevel(deck),
        )
        levelsFactory.createLevel(deck2)

        val got = levelsRepo.getLevelsByDeck(deck.id)
        assertEquals(3, got.size)
        assertEquals(levels, got)
    }
}