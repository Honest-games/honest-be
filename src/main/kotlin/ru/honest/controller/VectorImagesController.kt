package ru.honest.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.honest.exception.HonestEntityNotFound
import ru.honest.mybatis.repo.VectorImageRepo

@RestController
@RequestMapping("/api/v1/vector-images")
class VectorImagesController(
    private val vectorImageRepo: VectorImageRepo
) {
    @GetMapping("/{id}")
    fun getImage(
        @PathVariable("id") id: String
    ): ResponseEntity<String> {
        val image = vectorImageRepo.getImageById(id) ?: throw HonestEntityNotFound("Image $id not found")
        return ResponseEntity.ok(image)
    }
}