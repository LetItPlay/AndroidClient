package com.letitplay.maugry.letitplay.utils.ext


fun Boolean.ifTrue(block: () -> Unit) {
    if (this) {
        block()
    }
}