package com.letitplay.maugry.letitplay.utils


data class Optional<out T> private constructor(val value: T?) {
    companion object {
        fun <T> of(value: T?) = Optional(value)
        fun <T> none() = Optional<T>(null)
    }
}