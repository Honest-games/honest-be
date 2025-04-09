package ru.honest.repo

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.repo.DecksRepo
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class DecksRepoTest(
    private val decksRepo: DecksRepo,
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
): BaseTest() {
    @MethodSource("getDecksSource")
    @ParameterizedTest(name = "{0}")
    fun `getDecks works correct`(name: String, tc: DecksTestCase) {
        val created: List<DecksTestCaseData> = tc.setup(decksFactory, levelsFactory)
        val expected = created.filter { x -> x.shouldBeReturned }.map { x -> x.item }
        val result = tc.getData(decksRepo)
        assertEquals(expected.sortedBy { it.id }, result.sortedBy { it.id })
    }

    companion object {
        @JvmStatic
        fun getDecksSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "all with empty params", DecksTestCase(
                        { it, _ ->
                            listOf(
                                DecksTestCaseData(it.createDeck("1"), true),
                                DecksTestCaseData(it.createDeck("2"), true),
                                DecksTestCaseData(it.createDeck("3"), true),
                            )
                        },
                        { it.getDecks() }
                    )),
                Arguments.of(
                    "promo", DecksTestCase(
                        { it, _ ->
                            listOf(
                                DecksTestCaseData(it.createDeck("1", true, "p1"), true),
                                DecksTestCaseData(it.createDeck("2", true, "p2"), false),
                                DecksTestCaseData(it.createDeck("3", true), false),
                            )
                        },
                        { it.getDecks(promo = "p1") },
                    )
                ),
                Arguments.of(
                    "id", DecksTestCase(
                        { it, _ ->
                            listOf(
                                DecksTestCaseData(it.createDeck("1", true), true),
                                DecksTestCaseData(it.createDeck("2", true), false),
                                DecksTestCaseData(it.createDeck("3", true), false),
                            )
                        },
                        { it.getDecks(id = "1") },
                    )
                ),
            )
        }
    }
    data class DecksTestCase(
        val setup: (df: DecksFactory, f: LevelsFactory) -> List<DecksTestCaseData>,
        val getData: (r: DecksRepo) -> List<DeckModel>
    )

    data class DecksTestCaseData(
        val item: DeckModel,
        val shouldBeReturned: Boolean,
    )

}