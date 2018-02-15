package com.letitplay.maugry.letitplay

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.letitplay.maugry.letitplay.data_management.api.postServiceImpl
import com.letitplay.maugry.letitplay.data_management.api.serviceImpl
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.user_flow.Router
import com.letitplay.maugry.letitplay.user_flow.ui.ViewModelFactory
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
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
        ViewModelFactory(
                trendRepository,
                channelRepository,
                trackRepository,
                feedRepository,
                profileRepository,
                schedulerProvider
        )
    }

    private val trendRepository: TrendRepository by lazy { TrendDataRepository(db, serviceImpl, schedulerProvider, preferenceHelper) }
    private val channelRepository: ChannelRepository by lazy { ChannelDataRepository(db, serviceImpl, postServiceImpl, schedulerProvider, preferenceHelper) }
    private val trackRepository: TrackRepository by lazy { TrackDataRepository(db, postServiceImpl, schedulerProvider) }
    private val feedRepository: FeedRepository by lazy { FeedDataRepository(db, serviceImpl, schedulerProvider, preferenceHelper) }
    private val profileRepository: ProfileRepository by lazy { ProfileDataRepository(db, schedulerProvider, preferenceHelper) }

    private val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(applicationContext) }

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