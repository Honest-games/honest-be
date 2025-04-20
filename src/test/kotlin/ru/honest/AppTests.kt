package ru.honest

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL

@TestConstructor(autowireMode = ALL)
class AppTests: BaseTest() {

    @Test
    fun contextLoads() {
    }

}
