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
import ru.honest.factory.QuestionsFactory
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.model.QuestionModel
import ru.honest.mybatis.repo.LevelsRepo
import ru.honest.mybatis.repo.QuestionsRepo
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestConstructor(autowireMode = ALL)
class QuestionsRepoTest(
    private val decksFactory: DecksFactory,
    private val levelsFactory: LevelsFactory,
    private val questionsFactory: QuestionsFactory,
    private val levelsRepo: LevelsRepo,
    private val questionsRepo: QuestionsRepo,
) : BaseTest() {

    @MethodSource("getByParamsSource")
    @ParameterizedTest(name = "{0}")
    fun `getByParams works correct`(name: String, tc: QuestionTestCase) {
        val created: List<QuestionTestCaseData> = tc.setup(questionsFactory, levelsFactory, decksFactory)
        val expected = created.filter { x -> x.shouldBeReturned }.map { x -> x.item }
        val result: List<QuestionModel> = tc.getData(questionsRepo)
        assertEquals(expected.sortedBy { it.id }, result.sortedBy { it.id })
    }

    companion object {
        @JvmStatic
        fun getByParamsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "all with empty params", QuestionTestCase(
                    { q, _, _ ->
                        listOf(
                            QuestionTestCaseData(q.createQuestion(), true),
                            QuestionTestCaseData(q.createQuestion(), true),
                            QuestionTestCaseData(q.createQuestion(), true),
                            QuestionTestCaseData(q.createQuestion(), true),
                        )
                    },
                    { it.getByParams() }
                )),
                Arguments.of(
                    "levelsIds empty", QuestionTestCase(
                        { q, l, d ->
                            listOf(
                                QuestionTestCaseData(q.createQuestion(), false),
                                QuestionTestCaseData(q.createQuestion(), false),
                                QuestionTestCaseData(q.createQuestion(), false),
                            )
                        },
                        { it.getByParams(levelsIds = listOf()) },
                    )
                ),
                Arguments.of(
                    "levelsIds set", QuestionTestCase(
                        { q, l, d ->
                            val level = l.createLevel(id = "1")
                            val level2 = l.createLevel(id = "2")
                            listOf(
                                QuestionTestCaseData(q.createQuestion(level), true),
                                QuestionTestCaseData(q.createQuestion(level2), false),
                                QuestionTestCaseData(q.createQuestion(level), true),
                                QuestionTestCaseData(q.createQuestion(), false),
                            )
                        },
                        { it.getByParams(levelsIds = listOf("1")) },
                    )
                ),
            )
        }
    }
}

data class QuestionTestCase(
    val setup: (f: QuestionsFactory, lf: LevelsFactory, df: DecksFactory) -> List<QuestionTestCaseData>,
    val getData: (r: QuestionsRepo) -> List<QuestionModel>
)

data class QuestionTestCaseData(
    val item: QuestionModel,
    val shouldBeReturned: Boolean,
)
