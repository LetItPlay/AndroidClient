package com.letitplay.maugry.letitplay

import io.reactivex.Scheduler
import java.util.concurrent.Executor


interface SchedulerProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
    fun ioExecutor(): Executor
    fun uiExecutor(): Executor
}