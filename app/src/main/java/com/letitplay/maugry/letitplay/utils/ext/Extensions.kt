package com.letitplay.maugry.letitplay.utils.ext

fun String.splitTags(): List<String> =
        this.split(",")
                .map(String::trim)
                .filter(String::isNotEmpty)

fun List<Int>.joinWithComma() = this.joinToString(",")