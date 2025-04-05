package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.UsedQuestion

@Mapper
interface UsedQuestionsRepo {
    fun getUsedQuestions(
        clientId: String?,
        levelsIds: Set<String>? = null,
    ): List<UsedQuestion>

    fun save(usedQuestion: UsedQuestion)

    fun clearUsedQuestions(levelsIds: Set<String>, clientId: String)
}