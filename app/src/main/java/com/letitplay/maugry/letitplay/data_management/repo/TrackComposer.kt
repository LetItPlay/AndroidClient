package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject

typealias LikesState = SetState<Like>

inline fun <S, T> updateIfContains(collection: MutableList<T>, set: Set<S>, containFilter: ((S, T) -> Boolean), f: ((S, T) -> T)) {
    set.forEach { setItem ->
        val itemIndex = collection.indexOfFirst { containFilter(setItem, it) }
        if (itemIndex != -1) {
            val item = collection[itemIndex]
            collection[itemIndex] = f(setItem, item)
        }
    }
}

fun isLikeForTrack(like: Like, trackWithChannel: TrackWithChannel): Boolean {
    return like.trackId == trackWithChannel.track.id
}

fun likeTrack(like: Like, trackWithChannel: TrackWithChannel): TrackWithChannel {
    val trackObj = trackWithChannel.track
    return trackWithChannel.copy(likeId = like.trackId, track = trackObj.copy(likeCount = trackObj.likeCount + 1))
}

fun dislikeTrack(like: Like, trackWithChannel: TrackWithChannel): TrackWithChannel {
    val trackObj = trackWithChannel.track
    return trackWithChannel.copy(likeId = null, track = trackObj.copy(likeCount = trackObj.likeCount - 1))
}

fun Flowable<List<Like>>.switchApplyLikes(tracks: Single<List<TrackWithChannel>>): Flowable<List<TrackWithChannel>> {

    return compose { likesFlowable ->
        val tracksSubject = ReplaySubject.create<List<TrackWithChannel>>()
        scan(LikesState(), { oldState, newLikesCollection ->
            LikesState(oldState.new, newLikesCollection.toSet())
        })
                .skip(2)
                .switchMap {
                    val newLikes = it.new - it.old
                    val newDislikes = it.old - it.new
                    if (newLikes.isEmpty() && newDislikes.isEmpty())
                        return@switchMap Flowable.just(tracksSubject.value)
                    val updatedTracks = tracksSubject.value.map { track ->
                        val like = Like(track.track.id)
                        when {
                            newLikes.contains(like) -> likeTrack(like, track)
                            newDislikes.contains(like) -> dislikeTrack(like, track)
                            else -> track
                        }
                    }
                    tracksSubject.onNext(updatedTracks)
                    Flowable.just(tracksSubject.value)
                }
                .startWith(tracks.map {
                    val likes = likesFlowable.blockingFirst().associateBy { it.trackId }
                    val tracksWithLikes = it.map { track ->
                        track.copy(likeId = likes[track.track.id]?.trackId)
                    }
                    tracksSubject.onNext(tracksWithLikes)
                    tracksWithLikes
                }.toFlowable())
    }
}