package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.GL_SCHEDULER_REALM
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Callable


abstract class BaseManager {

    /**
     *   ENTRY POINT
     *
     *   @param local       функция, в которой нужно предоставить локальные данные в списке
     *   @param remoteWhen    функция для локальных данных (optional), которая решает нужно ли
     *                      (true/false) запрашивать remote
     *   @param remote      observable от remote
     *   @param update      функция для удаленных данных, для обновления локального репозитория
     *   @param error       функция для ошибки; примещивается в дополнение к сигналу от менеджера
     * */
    protected fun <T> get(
            local: (() -> List<T>),
            remoteWhen: ((List<T>) -> Boolean)? = null,
            remote: Observable<List<T>>? = null,
            update: ((List<T>) -> Unit)? = null,
            error: ((Throwable) -> Unit)? = null
    )
            : Observable<List<T>> {

        return Observable
                .fromCallable(Callable(local))
                .subscribeOn(GL_SCHEDULER_REALM)
                .observeOn(Schedulers.computation())
                .concatMap {
                    (if (remote != null && remoteWhen?.invoke(it) == true)
                        remote else Observable.just(it))
                            .observeOn(GL_SCHEDULER_REALM)
                            .let {
                                if (update != null) it.doOnNext(Consumer(update))
                                else it
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError(error))
    }

    protected fun onError(additional: ((Throwable) -> Unit)?) = Consumer<Throwable>({
        if (additional != null) {
            Consumer<Throwable>(additional)
        }
        Timber.e("Error caught by data-layer (manager)!")
    })
}