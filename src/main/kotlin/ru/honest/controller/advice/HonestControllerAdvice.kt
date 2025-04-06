package ru.honest.controller.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.honest.controller.HonestError
import ru.honest.exception.HonestEntityNotFound

@ControllerAdvice
class HonestControllerAdvice {
    @ExceptionHandler(HonestEntityNotFound::class)
    fun handleEntityNotFound(e: HonestEntityNotFound): ResponseEntity<HonestError> {
        return ResponseEntity(HonestError(e.message), HttpStatus.BAD_REQUEST)
    }
}