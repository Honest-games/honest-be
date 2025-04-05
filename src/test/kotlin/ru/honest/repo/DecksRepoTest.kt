package ru.honest.repo

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.honest.BaseTest
import ru.honest.mybatis.repo.DecksRepo

@TestConstructor(autowireMode = ALL)
class DecksRepoTest(
    private val decksRepo: DecksRepo
): BaseTest() {
    @Test
    fun `test decks by id`() {

    }
}