package com.letitplay.maugry.letitplay.data_management.repo.profile

import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Response

interface ProfileRepository {
    fun likedTracks(): Flowable<List<TrackWithChannel>>
    fun getLanguage(): Flowable<Optional<Language>>
    fun changeLanguage(language: Language): Completable
    fun putIsAdult(): Single<Response<Any>>
    fun deleteIsAdult(): Single<Response<Any>>
    fun signUp(uuid: String, username: String): Single<Response<Any>>
    fun signIn(uuid: String, username: String): Single<Response<Any>>
}