package ru.honest.factory

open class BaseFactory {
    protected var num: Int = 0
        get() = ++field
}
