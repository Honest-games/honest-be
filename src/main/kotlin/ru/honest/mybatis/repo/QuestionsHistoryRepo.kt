package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import ru.honest.mybatis.model.QuestionHistoryModel

@Mapper
interface QuestionsHistoryRepo {
    fun save(questionHistory: QuestionHistoryModel)
    fun findAll(): List<QuestionHistoryModel>
}