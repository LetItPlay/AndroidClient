package com.letitplay.maugry.letitplay.data_management.repo


data class SetState<out T>(val old: Set<T> = emptySet(), val new: Set<T> = emptySet())