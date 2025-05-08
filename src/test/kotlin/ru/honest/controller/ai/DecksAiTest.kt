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
    @MethodSource("deckTypeTestCases")
    fun `deck type test`(name: String, testCase: AiDeckTypeTestCase) {
        val d = decksFactory.createDeck(aiType = testCase.deckAiType)
        testCase.deckPromo?.let {
            promoFactory.createPromo(deckId = d.id, promoCode = testCase.deckPromo)
        }
        enterPromo(testCase.enterPromoClientId, testCase.enteredPromo)
        val deck = getDecks(testCase.requesterClientId).first()
        assert(deck.aiType == testCase.expectedDeckAiType) {
            "Expected deck AI type ${testCase.expectedDeckAiType}, but got ${deck.aiType}"
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("aiGenerateTestCases")
    fun `ai generate test`(name: String, testCase: AiGenerateTestCase) {
        val d = decksFactory.createDeck(aiType = testCase.deckAiType)
        promoFactory.createPromo(deckId = d.id, promoCode = "prom")
        val l = List(3) { levelsFactory.createLevel(deck = d) }.random()
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
        data class AiDeckTypeTestCase(
            val deckPromo: String? = null,
            val enteredPromo: String? = null,
            val enterPromoClientId: String? = null,
            val requesterClientId: String,
            val deckAiType: DeckAiType,
            val expectedDeckAiType: DeckAiType,
        )

        @JvmStatic
        fun deckTypeTestCases() = listOf(
            Arguments.of(
                "ai-extended - ai-extended",
                AiDeckTypeTestCase(
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.AI_EXTENDED,
                    expectedDeckAiType = DeckAiType.AI_EXTENDED,
                )
            ),
            Arguments.of(
                "ai-only - ai-only",
                AiDeckTypeTestCase(
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.AI_ONLY,
                    expectedDeckAiType = DeckAiType.AI_ONLY,
                )
            ),
            Arguments.of(
                "non-ai - non-ai",
                AiDeckTypeTestCase(
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.NON_AI,
                    expectedDeckAiType = DeckAiType.NON_AI,
                )
            ),
            Arguments.of(
                "non-ai,ai-extend promo - becomes ai-extended",
                AiDeckTypeTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.NON_AI,
                    expectedDeckAiType = DeckAiType.AI_EXTENDED,
                )
            ),
            Arguments.of(
                "ai-extended,ai-extend promo - ai-extended",
                AiDeckTypeTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.AI_EXTENDED,
                    expectedDeckAiType = DeckAiType.AI_EXTENDED,
                )
            ),
            Arguments.of(
                "ai-only,ai-extend promo - ai-only",
                AiDeckTypeTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.AI_ONLY,
                    expectedDeckAiType = DeckAiType.AI_ONLY,
                )
            ),
            Arguments.of(
                "non-ai,wrong promo - non-ai",
                AiDeckTypeTestCase(
                    deckPromo = "prom",
                    enteredPromo = "wrong",
                    enterPromoClientId = "client1",
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.NON_AI,
                    expectedDeckAiType = DeckAiType.NON_AI,
                )
            ),
            Arguments.of(
                "non-ai,ai-extend promo,other user - non-ai",
                AiDeckTypeTestCase(
                    deckPromo = "prom",
                    enteredPromo = "prom",
                    enterPromoClientId = "client2",
                    requesterClientId = "client1",
                    deckAiType = DeckAiType.NON_AI,
                    expectedDeckAiType = DeckAiType.NON_AI,
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
                "non-ai,ai gen - non-ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isAiRequested = true,
                    isAiQuestionExpected = false,
                )
            ),
            Arguments.of(
                "non-ai,ai promo,ai gen - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isPromoCorrect = true,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-extended,ai gen - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_EXTENDED,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-only,ai gen - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-only,usual - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = false,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "non-ai,ai promo,usual - usual",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.NON_AI,
                    isPromoCorrect = true,
                    isAiRequested = false,
                    isAiQuestionExpected = false,
                )
            ),
            Arguments.of(
                "non-ai,ai promo,ai gen,other user - usual",
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
                "ai-only,ai gen - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = true,
                    isAiQuestionExpected = true,
                )
            ),
            Arguments.of(
                "ai-only,usual - ai",
                AiGenerateTestCase(
                    deckAiType = DeckAiType.AI_ONLY,
                    isAiRequested = false,
                    isAiQuestionExpected = true,
                )
            ),
        )
    }
}