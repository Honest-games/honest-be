package ru.honest.factory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.honest.mybatis.model.DeckModel
import ru.honest.mybatis.model.LevelModel
import ru.honest.mybatis.repo.QuestionModel
import ru.honest.mybatis.repo.QuestionsRepo
import java.util.*

@Component
class QuestionsFactory(
    @Lazy
    private val levelsFactory: LevelsFactory,
    @Lazy
    private val decksFactory: DecksFactory,
    private val questionsRepo: QuestionsRepo,
): BaseFactory() {
    fun createQuestion(
        level: LevelModel = levelsFactory.createLevel(),
    ): QuestionModel {
        val q = QuestionModel(
            id = UUID.randomUUID().toString(),
            levelId = level.id,
            text = "text $num",
            additionalTest = null,
        )
        questionsRepo.save(q)
        return q
    }
}