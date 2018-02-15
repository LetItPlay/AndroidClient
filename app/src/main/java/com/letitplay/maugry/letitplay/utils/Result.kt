package com.letitplay.maugry.letitplay.utils

import com.letitplay.maugry.letitplay.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

sealed class Result<T>(val inProgress: Boolean) {
    class InProgress<T> : Result<T>(true)
    data class Success<T>(var data: T) : Result<T>(false)
    data class Failure<T>(val errorMessage: String?, val e: Throwable): Result<T>(false)

    companion object {
        fun <T> inProgress(): Result<T> = InProgress()
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(errorMessage: String?, e: Throwable): Result<T> = Failure(errorMessage, e)
    }
}


fun <T> Flowable<T>.toResult(schedulerProvider: SchedulerProvider):
        Flowable<Result<T>> {
    return compose { item ->
        item
                .map { Result.success(it) }
                .onErrorReturn { e -> Result.failure(e.message ?: "unknown", e) }
                .observeOn(schedulerProvider.ui())
                .startWith(Result.inProgress())
    }
}

fun <T> Observable<T>.toResult(schedulerProvider: SchedulerProvider):
        Observable<Result<T>> {
    return compose { item ->
        item
                .map { Result.success(it) }
                .onErrorReturn { e -> Result.failure(e.message ?: "unknown", e) }
                .observeOn(schedulerProvider.ui())
                .startWith(Result.inProgress())
    }
}

fun <T> Single<T>.toResult(schedulerProvider: SchedulerProvider):
        Observable<Result<T>> {
    return toObservable().toResult(schedulerProvider)
}

fun <T> Completable.toResult(schedulerProvider: SchedulerProvider):
        Observable<Result<T>> {
    return toObservable<T>().toResult(schedulerProvider)
}