package com.letitplay.maugry.letitplay.data_management.service


import android.util.Log
import com.github.salomonbrys.kotson.DeserializerArg
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.letitplay.maugry.letitplay.GL_DATA_SERVICE_URL
import com.letitplay.maugry.letitplay.GL_SCHEDULER_IO
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


private fun <T> response(): (DeserializerArg) -> T? = {
    // todo: error?
    GsonBuilder()
            .create()
            .fromJson<T>(it.json.asJsonObject.get("response"), it.type)
}

private val gson = GsonBuilder()
        .registerTypeAdapter<List<ChannelModel>> { deserialize(response()) }
        .create()

private val service = Retrofit.Builder()
        .baseUrl(GL_DATA_SERVICE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Service::class.java)

interface Service {
    @GET("stations")
    fun channels(): Observable<List<ChannelModel>>

    @POST("stations/{id}/counts/")
    fun updateChannelFollowers(@Path("id") idStation: Int, @Body followers: FollowersModel): Observable<ChannelModel>

    @POST("tracks/{id}/counts/")
    fun updateFavouriteTracks(@Path("id") idTrack: Int, @Body likes: LikeModel): Observable<TrackModel>

    @GET("tracks/stations/{id}")
    fun getPieceTracks(@Path("id") idStation: Int): Observable<List<TrackModel>>

    @GET("tracks")
    fun getTracks(): Observable<List<TrackModel>>
}

object ServiceController : BaseServiceController() {

    fun getChannels(): Observable<List<ChannelModel>> {
        return get(service.channels())
    }

    fun updateChannelFollowers(id: Int, body: FollowersModel): Observable<ChannelModel> {
        return get(service.updateChannelFollowers(id, body))
    }

    fun updateFavouriteTracks(id: Int, body: LikeModel): Observable<TrackModel> {
        return get(service.updateFavouriteTracks(id, body))
    }

    fun getTracks(): Observable<List<TrackModel>> {
        return get(service.getTracks())
    }

    fun getPieceTracks(id: Int): Observable<List<TrackModel>> {
        return get(service.getPieceTracks(id))
    }
}


abstract class BaseServiceController {
    protected fun <T> get(observable: Observable<T>): Observable<T> = observable
            .subscribeOn(GL_SCHEDULER_IO)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.d("ServiceController", "Something go wrong")
            }
}
