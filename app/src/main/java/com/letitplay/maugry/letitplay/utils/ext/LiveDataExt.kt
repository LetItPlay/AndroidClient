package com.letitplay.maugry.letitplay.utils.ext

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.internal.disposables.DisposableContainer


fun <T> Flowable<T>.toLiveData(disposableContainer: DisposableContainer): LiveData<T> {
    val mutableLiveData = MutableLiveData<T>()
    disposableContainer.add(this.subscribe {
        mutableLiveData.value = it
    })
    return mutableLiveData
}