package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


open class BaseViewModel: ViewModel() {
    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}