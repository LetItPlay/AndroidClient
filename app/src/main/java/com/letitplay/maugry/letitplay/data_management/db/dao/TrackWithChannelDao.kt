package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


@Dao
abstract class TrackWithChannelDao {
    @Query("SELECT tracks.*, channels.*, likes.track_id as likeId FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId " +
            "INNER JOIN follows ON follows.channelId = channels.channel_id " +
            "LEFT JOIN likes ON likes.track_id = tracks.track_id " +
            "WHERE tracks.track_lang = :lang")
    abstract fun getAllTracksWithFollowedChannels(lang: Language): DataSource.Factory<Int, TrackWithChannel>

    @Query("SELECT tracks.*, channels.*, likes.track_id as likeId FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId " +
            "LEFT JOIN likes ON likes.track_id = tracks.track_id " +
            "WHERE tracks.track_lang = :lang")
    abstract fun getAllTracks(lang: Language): Flowable<List<TrackWithChannel>>

    @Query("SELECT tracks.*, channels.*, likes.track_id as likeId FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId " +
            "INNER JOIN likes ON likes.track_id = tracks.track_id " +
            "WHERE tracks.track_lang = :lang")
    abstract fun getLikedTracks(lang: Language): Flowable<List<TrackWithChannel>>
}