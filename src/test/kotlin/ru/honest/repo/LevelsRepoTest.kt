package ru.honest.repo

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.repo.LevelsRepo
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class LevelsRepoTest(
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val levelsRepo: LevelsRepo,
) : BaseTest() {
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

    @MethodSource("getByParamsSource")
    @ParameterizedTest(name = "{0}")
    fun `getByParams works correct`(name: String, tc: TestCase) {
        val created: List<TestCaseData> = tc.setup(levelsFactory, decksFactory)
        val expected = created.filter { x -> x.shouldBeReturned }.map { x -> x.item }
        val result: List<LevelModel> = tc.getData(levelsRepo)
        assertEquals(expected.sortedBy { it.id }, result.sortedBy { it.id })
    }

    companion object {
        @JvmStatic
        fun getByParamsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "all with empty params", TestCase(
                    { it, _ ->
                        listOf(
                            TestCaseData(it.createLevel(), true),
                            TestCaseData(it.createLevel(), true),
                            TestCaseData(it.createLevel(), true),
                        )
                    },
                    { it.getByParams() }
                )),
                Arguments.of(
                    "decksIds", TestCase(
                        { it, decks ->
                            val deck1 = decks.createDeck("1")
                            val deck2 = decks.createDeck("2")
                            val deck3 = decks.createDeck("3")
                            listOf(
                                TestCaseData(it.createLevel(deck1), true),
                                TestCaseData(it.createLevel(deck2), false),
                                TestCaseData(it.createLevel(deck1), true),
                                TestCaseData(it.createLevel(deck2), false),
                                TestCaseData(it.createLevel(deck3), true),
                            )
                        },
                        { it.getByParams(decksIds = listOf("1", "3")) },
                    )
                )
            )
        }
    }

    data class TestCase(
        val setup: (f: LevelsFactory, df: DecksFactory) -> List<TestCaseData>,
        val getData: (r: LevelsRepo) -> List<LevelModel>
    )

    data class TestCaseData(
        val item: LevelModel,
        val shouldBeReturned: Boolean,
    )
}