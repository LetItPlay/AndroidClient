package com.letitplay.maugry.letitplay.utils.ext

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import org.reactivestreams.Publisher

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>
fun <T> Publisher<T>.toMutableLiveData() = LiveDataReactiveStreams.fromPublisher(this) as MutableLiveData<T>