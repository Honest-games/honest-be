package ru.honest.mybatis.repo

import org.apache.ibatis.annotations.Mapper
import org.springframework.http.ResponseEntity
import ru.honest.controller.DeckOutput

@Mapper
interface VectorImageRepo{
    fun getImageById(id: String): String?
    fun create(id: String, content: String)
}
