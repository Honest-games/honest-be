package ru.honest.controller.ai

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.factory.DecksFactory
import ru.honest.factory.LevelsFactory
import ru.honest.factory.PromoFactory
import ru.honest.factory.QuestionsFactory
import ru.honest.mybatis.model.DeckAiType
import ru.honest.mybatis.model.DeckAiUserAccessType
import ru.honest.service.gpt.GptClientStub

@TestConstructor(autowireMode = ALL)
class DecksAiTest(
    private val decksFactory: DecksFactory,
    private val questionsFactory: QuestionsFactory,
    private val levelsFactory: LevelsFactory,
    private val gptClientStub: GptClientStub,
    private var promoFactory: PromoFactory
): BaseTest() {
    @ParameterizedTest(name = "{0}")
    @MethodSource("nonAiAccessTestCases")
    fun `non-ai access test`(name: String, testCase: AiAccessTestCase) {
        val d = decksFactory.createDeck()
        testCase.deckPromo?.let {
            promoFactory.createPromo(deckId = d.id, promoCode = testCase.deckPromo)
        }
        enterPromo(testCase.enterPromoClientId, testCase.enteredPromo)
        val deck = getDecks(testCase.requesterClientId).first()
        assert(deck.aiType == testCase.expectedDeckAiAccessType) {
            "Expected deck AI type ${testCase.expectedDeckAiAccessType}, but got ${deck.aiType}"
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("aiGenerateTestCases")
    fun `ai generate test`(name: String, testCase: AiGenerateTestCase) {
        val d = decksFactory.createDeck(aiType = testCase.deckAiType)
        promoFactory.createPromo(deckId = d.id, promoCode = "prom")
        val l = levelsFactory.createLevel(deck = d)
        val q = questionsFactory.createQuestion(level = l)
        val stubResponse = "stubb"
        gptClientStub.setStubResponse(stubResponse)

        testCase.isPromoCorrect?.let {
            enterPromo(testCase.enterPromoClientId, if(testCase.isPromoCorrect) "prom" else "wrong")
        }
        val deck = getDecks(testCase.requesterClientId).first()
        val randQuestion = getRandQuestion(testCase.requesterClientId, l.id, testCase.isAiRequested)
        if(testCase.isAiQuestionExpected){
            assert(randQuestion.text == stubResponse) { "Expected AI question, but got $randQuestion" }
        } else {
            assert(randQuestion.text == q.text) { "Expected non-AI question, but got $randQuestion" }
        }
    }

    companion object {
        data class AiAccessTestCase(
            val deckPromo: String?,
            val enteredPromo: String? = null,
            val enterPromoClientId: String? = null,
            val requesterClientId: String,
            val expectedDeckAiAccessType: DeckAiUserAccessType,
        )

        @JvmStatic
        fun nonAiAccessTestCases() = listOf(
            Arguments.of(
                "becomes ai-extended after needed promo",
                AiAccessTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    expectedDeckAiAccessType = DeckAiUserAccessType.AI_EXTENDED,
                )
            ),
            Arguments.of(
                "non-ai after wrong promo",
                AiAccessTestCase(
                    deckPromo = "prom",
                    enteredPromo = "wrong",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    expectedDeckAiAccessType = DeckAiUserAccessType.NON_AI,
                )
            ),
            Arguments.of(
                "non-ai for other user",
                AiAccessTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client2",
                    requesterClientId = "client1",
                    expectedDeckAiAccessType = DeckAiUserAccessType.NON_AI,
                )
            ),
        )

        data class AiGenerateTestCase(
            val deckAiType: DeckAiType,
            val isPromoCorrect: Boolean? = null,
            val enterPromoClientId: String = "client1",
            val requesterClientId: String = "client1",
            val isAiRequested: Boolean,
            val isAiQuestionExpected: Boolean,
        )

        @JvmStatic
        fun aiGenerateTestCases() = listOf(
            Arguments.of(
                "non-ai deck - doesn't gen ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isAiRequested = true,
                    isAiQuestionExpected = false,
                )
            ),
            Arguments.of(
                "ai-extended deck - gen ai if requested",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isPromoCorrect = true,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-extended deck - gen usual question if requested",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isPromoCorrect = true,
                    isAiRequested = false,
                    isAiQuestionExpected = false,
                )
            ),
            Arguments.of(
                "ai-extended deck - doesn't gen ai for other user",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isPromoCorrect = true,
                    enterPromoClientId = "client2",
                    requesterClientId = "client1",
                    isAiRequested = true,
                    isAiQuestionExpected = false,
                )
            ),
            Arguments.of(
                "ai-only deck - gen ai if requested",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-only deck - gen ai if not requested",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = false,
                    isAiQuestionExpected = true,
                )
            ),
        )
    }
}