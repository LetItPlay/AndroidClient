package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.arch.lifecycle.MutableLiveData


class InitializedMutableLiveData<T>: MutableLiveData<T> {
    constructor(item: T) {
        value = item
    }
}