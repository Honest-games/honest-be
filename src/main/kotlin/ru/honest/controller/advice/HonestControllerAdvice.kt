package ru.honest.controller.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.honest.controller.HonestError
import ru.honest.exception.HonestEntityNotFound
import ru.honest.exception.InvalidPromoCodeException
import ru.honest.exception.PromoAlreadyExistsException

@ControllerAdvice
class HonestControllerAdvice {
    @ExceptionHandler(HonestEntityNotFound::class)
    fun handleEntityNotFound(e: HonestEntityNotFound): ResponseEntity<HonestError> {
        return ResponseEntity(HonestError(e.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InvalidPromoCodeException::class)
    fun handleInvalidPromoCode(e: InvalidPromoCodeException): ResponseEntity<HonestError> {
        return ResponseEntity(HonestError(e.message ?: "Invalid promo code"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PromoAlreadyExistsException::class)
    fun handlePromoAlreadyExists(e: PromoAlreadyExistsException): ResponseEntity<HonestError> {
        return ResponseEntity(HonestError(e.message ?: "Promo code already exists"), HttpStatus.CONFLICT)
    }
}