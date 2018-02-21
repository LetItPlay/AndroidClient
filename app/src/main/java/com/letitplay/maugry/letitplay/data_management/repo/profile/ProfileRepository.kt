package com.letitplay.maugry.letitplay.data_management.repo.profile

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable

interface ProfileRepository {
    fun likedTracks(): Flowable<List<TrackWithChannel>>
}