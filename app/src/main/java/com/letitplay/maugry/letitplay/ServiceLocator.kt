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
import com.letitplay.maugry.letitplay.user_flow.ui.ViewModelFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@SuppressLint("StaticFieldLeak")
object ServiceLocator {
    lateinit var applicationContext: Context

    val viewModelFactory by lazy {
        ViewModelFactory(trendRepository, channelRepository)
    }

    val trendRepository: TrendRepository by lazy { DbTrendRepository(db, serviceImpl, ioExecutor, mainThreadExecutor) }
    val channelRepository: ChannelRepository by lazy { DbChannelRepository(db, serviceImpl, postServiceImpl) }

    val db: LetItPlayDb by lazy {
        Room.databaseBuilder(applicationContext, LetItPlayDb::class.java, "letitplay.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    val ioExecutor by lazy { Executors.newSingleThreadExecutor() }


    val mainThreadExecutor: Executor by lazy { MainThreadExecutor() }

    internal class MainThreadExecutor : Executor {

        private val handler = Handler(Looper.getMainLooper())

        override fun execute(runnable: Runnable) {
            handler.post(runnable)
        }
    }
}