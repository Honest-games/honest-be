package ru.honest.exception

sealed class PromoException(message: String) : RuntimeException(message)

class InvalidPromoCodeException(message: String) : PromoException(message)

class PromoAlreadyExistsException(message: String) : PromoException(message)