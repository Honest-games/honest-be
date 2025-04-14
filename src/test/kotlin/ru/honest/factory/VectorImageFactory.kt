package ru.honest.factory

import org.springframework.stereotype.Component
import ru.honest.mybatis.repo.VectorImageRepo

@Component
class VectorImageFactory(
    private val vectorImageRepo: VectorImageRepo
): BaseFactory() {
    fun createImage(id: String, content: String){
        vectorImageRepo.create(id, content)
    }
}