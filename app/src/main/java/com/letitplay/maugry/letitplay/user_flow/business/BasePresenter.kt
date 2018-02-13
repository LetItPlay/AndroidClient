package com.letitplay.maugry.letitplay.user_flow.business

import android.app.AlertDialog
import android.content.Context
import android.support.v7.view.ContextThemeWrapper
import com.letitplay.maugry.letitplay.GL_ALERT_DIALOG_DELAY
import com.letitplay.maugry.letitplay.GL_PRESENTER_ACTION_RETRY_COUNT
import com.letitplay.maugry.letitplay.GL_PRESENTER_ACTION_RETRY_DELAY
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.api.BaseServiceController
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 *      CORE PRESENTER
 *      USEFUL DATA/VIEW PROXY & EXECUTION HANDLER
 *
 *      - it handles async requests via @execute mapper
 *      - ...
 * */
abstract class BasePresenter<V>(
        val service: BaseServiceController? = null) where V : IMvpView {

    
      val currentContentLang: Language?
        get() = context?.let { PreferenceHelper(it).contentLanguage }

    /*
    *       VIEW REF & SHORTHANDS
    * */
    protected var viewRef = WeakReference<V?>(null)

    @Suppress("UNCHECKED_CAST")
    protected inline val mvp
        get() = viewRef.get()?.safeView as? V?
    protected inline val context: Context? get() = mvp?.ctx

    /*
    *       NETWORKING
    * */
    private var httpFailedTask: (() -> Unit)? = null

    /*
    *       BASIC
    * */

    open fun apply(mvpView: V) {
        viewRef = WeakReference(mvpView)
    }


    /*
    *       VIEW
    * */

    protected fun stageProgress(show: Boolean = false, hide: Boolean = false, mvp: V?) {
        when {
            show -> mvp?.showProgress()
            hide -> mvp?.hideProgress()
        }
    }

    /*
    *       EXECUTION
    * */

    //todo: consider polymorphism rather than a mapper; or is it fine?
    protected fun <O> execute(conf: ExecutionConfig<O, V>): Flowable<O> {
        httpFailedTask = null

        conf.viewRef = viewRef

        val replaySubject = when (conf.replayTime) {
            null -> ReplaySubject.createWithSize<O>(conf.replaySize)
            else -> ReplaySubject.createWithTimeAndSize(
                    conf.replayTime, TimeUnit.MILLISECONDS, Schedulers.computation(), conf.replaySize
            )
        }

        conf.apply {

            /**
             *       EVENT ACTIONS
             * */

            val onSubscribe: () -> Unit = { stageProgress(show = triggerProgress, mvp = conf.mvp) }
            val onNext: (O) -> Unit = onNext(this, replaySubject)
            val onError: (Throwable) -> Unit = onError(this, replaySubject)
            val onComplete: () -> Unit = onComplete(this, replaySubject)

            /**
             *       PROVIDER MAPPING
             *
             *       - 0..1 provider will be used
             * */

            when {
                hasValidCache?.invoke() == true -> {
                    Single.just(cache)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { onSubscribe() }
                            .subscribe(
                                    {
                                        stageProgress(hide = triggerProgress, mvp = conf.mvp)
                                        if (conf.isAlive)
                                            conf.onCompleteWithContext?.invoke(conf.mvp)
                                        else
                                            Timber.e("Conf-context is dead, context-dependent action wont be invoked!")
                                        replaySubject.onComplete()
                                    },
                                    {}
                            )
                }

            // todo: maybe simplify to flowable?
                asyncObservable != null -> {
                    asyncObservable
                            .observeOn(AndroidSchedulers.mainThread())
                            .retryWhen {
                                it.zipWith(Observable.range(1, GL_PRESENTER_ACTION_RETRY_COUNT),
                                        { e, i ->
                                            when {
                                                i < GL_PRESENTER_ACTION_RETRY_COUNT ->
                                                    Observable.timer(GL_PRESENTER_ACTION_RETRY_DELAY, TimeUnit.MILLISECONDS)
                                                else ->
                                                    Observable.error(e)
                                            }
                                        }
                                ).flatMap { it }
                            }
                            // todo: полезная штука для тестирования стабильности работы цепочек
                            /*.delaySubscription(3, TimeUnit.SECONDS)*/
                            .doOnSubscribe { onSubscribe() }
                            .subscribe(
                                    onNext,
                                    onError,
                                    onComplete
                            )
                }

                else -> replaySubject.onError(Exception("Data provider is not supplied!"))
            }

        }

        return replaySubject.toFlowable(BackpressureStrategy.LATEST)
    }

    private fun <O> onNext(conf: ExecutionConfig<O, V>, replaySubject: ReplaySubject<O>): (O) -> Unit = { o ->
        conf.onNextNonContext?.invoke(o)
        if (conf.isAlive)
            conf.onNextWithContext?.invoke(conf.mvp, o)
        else
            Timber.e("Conf-context is dead, context-dependent action wont be invoked!")
        replaySubject.onNext(o)
    }

    private fun <O> onError(conf: ExecutionConfig<O, V>, replaySubject: ReplaySubject<O>): (Throwable) -> Unit = { throwable ->
        // todo: onError or onComplete makes RPS throw undeliverable error!!!
        Timber.e(throwable, throwable.message)
        if (conf.isAlive) {
            conf.onErrorWithContext?.invoke(conf.mvp, throwable)
            when (throwable) {
                is IOException,
                is SocketTimeoutException,
                is UnknownHostException -> {
                    httpFailedTask = { if (conf.isAlive) execute(conf) }
                    stageProgress(hide = conf.triggerProgress, mvp = conf.mvp)
                }
                else -> Observable
                        .create<Unit> {
                            if (conf.isAlive) AlertDialog
                                    .Builder(ContextThemeWrapper(conf.context, R.style.AlertDialog))
                                    .setCancelable(false)
                                    .apply {
                                        when (throwable) {
                                            is TimeoutException -> {
                                                setTitle("TimeoutException")
                                                setMessage("Задача не была выплнена за отведенное время," +
                                                        " обратитесь к поставщику приложения.")
                                            }
                                            is HttpException -> {
                                                setTitle("HttpException")
                                                setMessage("Сервис обработал запрос с исключением. " +
                                                        "Скорее всего мы уже в курсе и исправляем проблему. " +
                                                        "Если проблема повторяется длительное время," +
                                                        " обратитесь к поставщику приложения.")
                                            }
                                        /*is IOException,
                                        is SocketTimeoutException,
                                        is UnknownHostException -> {
                                            setTitle("IOException")
                                            setMessage("Сервис не доступен. Вы не в сети?")
                                            httpFailedTask = { execute(conf) }
                                        }*/
                                            else -> {
                                                setTitle("UnknownException")
                                                setMessage("Неизвестная ошибка (${throwable::class.java})," +
                                                        " обратитесь к поставщику приложения.")
                                            }
                                        }
                                    }
                                    .setPositiveButton("Повторить", { _, _ -> execute(conf) })
                                    .setNegativeButton("Отмена", { _, _ -> stageProgress(hide = conf.triggerProgress, mvp = conf.mvp) })
                                    .create()
                                    .show()

                            it.onComplete()
                        }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .delaySubscription(GL_ALERT_DIALOG_DELAY, TimeUnit.SECONDS)
                        .subscribe({}, {}, {})
            }
        } else {
            Timber.e("Action failed, mvp is detached!")
        }
    }

    private fun <O> onComplete(conf: ExecutionConfig<O, V>, replaySubject: ReplaySubject<O>): () -> Unit = {

        conf.onCompleteNonContext?.invoke()
        if (conf.isAlive)
            conf.onCompleteWithContext?.invoke(conf.mvp)
        else
            Timber.e("Conf-context is dead, context-dependent action wont be invoked!")
        // todo: onError or onComplete makes RPS throw undeliverable error!!!
        replaySubject.onComplete()
        stageProgress(hide = conf.triggerProgress, mvp = conf.mvp)
    }
}
