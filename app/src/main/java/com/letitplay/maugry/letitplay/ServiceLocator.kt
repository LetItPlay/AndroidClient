package com.letitplay.maugry.letitplay

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.letitplay.maugry.letitplay.data_management.api.postServiceImpl
import com.letitplay.maugry.letitplay.data_management.api.serviceImpl
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.DbChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.DbTrendRepository
import com.letitplay.maugry.letitplay.data_management.repo.TrendRepository
import com.letitplay.maugry.letitplay.user_flow.Router
import com.letitplay.maugry.letitplay.user_flow.ui.ViewModelFactory
import com.zhuinden.simplestack.BackstackDelegate
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@SuppressLint("StaticFieldLeak")
object ServiceLocator {
    lateinit var applicationContext: Context
    lateinit var backstackDelegate: BackstackDelegate

    val viewModelFactory by lazy {
        ViewModelFactory(trendRepository, channelRepository, schedulerProvider)
    }

    val trendRepository: TrendRepository by lazy { DbTrendRepository(db, serviceImpl, schedulerProvider) }
    val channelRepository: ChannelRepository by lazy { DbChannelRepository(db, serviceImpl, postServiceImpl, schedulerProvider) }

    val db: LetItPlayDb by lazy {
        Room.databaseBuilder(applicationContext, LetItPlayDb::class.java, "letitplay.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    val router = object : Router {
        override fun navigateTo(key: Any) {
            backstackDelegate.backstack.goTo(key)
        }
    }

    val schedulerProvider: SchedulerProvider by lazy {
        object: SchedulerProvider {
            private val mainThreadExecutor = MainThreadExecutor()
            private val mainThreadScheduler = Schedulers.from(mainThreadExecutor)
            private val ioExecutor = Executors.newSingleThreadExecutor()
            private val ioScheduler = Schedulers.from(ioExecutor)

            override fun ui(): Scheduler = mainThreadScheduler

            override fun io(): Scheduler = ioScheduler

            override fun ioExecutor(): Executor = ioExecutor

            override fun uiExecutor() = mainThreadExecutor
        }
    }

    internal class MainThreadExecutor : Executor {

        private val handler = Handler(Looper.getMainLooper())

        override fun execute(runnable: Runnable) {
            handler.post(runnable)
        }
    }
}