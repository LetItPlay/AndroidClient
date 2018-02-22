package com.letitplay.maugry.letitplay.data_management.repo.profile

import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable
import io.reactivex.Flowable

interface ProfileRepository {
    fun likedTracks(): Flowable<List<TrackWithChannel>>
    fun flipLanguage(): Completable
    fun getLanguage(): Flowable<Optional<Language>>
}