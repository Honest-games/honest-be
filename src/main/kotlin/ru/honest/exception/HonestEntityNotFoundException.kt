package ru.honest.exception

class HonestEntityNotFound(
    override val message: String
) : RuntimeException()