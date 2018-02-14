package com.letitplay.maugry.letitplay.data_management.api


import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.letitplay.maugry.letitplay.BuildConfig
import com.letitplay.maugry.letitplay.GL_DATA_SERVICE_URL
import com.letitplay.maugry.letitplay.GL_POST_REQUEST_SERVICE_URL
import com.letitplay.maugry.letitplay.GL_SCHEDULER_IO
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.api.responses.FeedResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedChannelResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedTrackResponse
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

private val logInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
}

private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .build()

private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .create()

private val serviceBuilder = Retrofit.Builder()
        .client(httpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))

val serviceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayApi::class.java)

private val postService = serviceBuilder
        .baseUrl(GL_POST_REQUEST_SERVICE_URL)
        .build()
        .create(LetItPlayPostApi::class.java)

interface LetItPlayApi {

    @GET("stations")
    fun channels(): Single<List<Channel>>

    @GET("stations/{id}/tracks")
    fun getChannelTracks(@Path("id") idStation: Int): Observable<Response<List<Track>>>

    @GET("tracks")
    fun getTracks(): Observable<List<Track>>

    @GET("feed?")
    fun getFeed(@Query("stIds") stIds: String, @Query("limit") limit: Int, @Query("lang") lang: String): Single<FeedResponse>

    @GET("abrakadabra?")
    fun getSearch(@Query("lang") lang: String): Observable<TrendResponse>

    @GET("trends/7?")
    fun trends(@Query("lang") lang: String): Single<TrendResponse>

    @GET("stations/{id}")
    fun getChannelPiece(@Path("id") channelId: Int): Maybe<Channel>
}

interface LetItPlayPostApi {
    @POST("stations/{id}/counts/")
    fun updateChannelFollowers(@Path("id") idStation: Int, @Body followers: UpdateFollowersRequestBody): Observable<UpdatedChannelResponse>

    @POST("tracks/{id}/counts/")
    fun updateFavouriteTracks(@Path("id") idTrack: Int, @Body likes: UpdateRequestBody): Observable<UpdatedTrackResponse>
}

//object ServiceController : BaseServiceController() {
//
//    fun getChannels(): Observable<List<Channel>> {
//        return get(service.channels())
//    }
//
//    fun updateChannelFollowers(id: Int, body: UpdateFollowersRequestBody): Observable<Channel> {
//        return get(postService.updateChannelFollowers(id, body).map(::toChannelModel))
//    }
//
//    fun updateFavouriteTracks(id: Int, body: UpdateRequestBody): Observable<TrackWithChannel> {
//        return get(postService.updateFavouriteTracks(id, body).map(::toTrackModel))
//    }
//
//    fun getTracks(): Observable<List<TrackWithChannel>> {
//        return get(service.getTracks())
//    }
//
//    fun getChannelTracks(idStation: Int): Observable<List<TrackWithChannel>> {
//        return get(service.getChannelTracks(idStation)).map {
//            if (it.body() != null) it.body() else emptyList()
//        }
//    }
//
//    fun getFeed(stIds: String, limit: Int, lang: String): Observable<TrendResponse> {
//        return get(service.getFeed(stIds, limit, lang))
//    }
//
//    fun getTrends(lang: String): Observable<TrendResponse> {
//        return get(service.getTrends(lang))
//    }
//
//    fun getSearch(lang: String): Observable<TrendResponse> {
//        return get(service.getSearch(lang))
//    }
//}
//
abstract class BaseServiceController {

    private val errorConsumer = Consumer<Throwable> { it ->
        when (it) {
            is TimeoutException,
            is HttpException,
            is IOException,
            is SocketTimeoutException,
            is UnknownHostException -> {
                Timber.e("LetItPlayApi related error!")
            }
            else -> Timber.e("Unknown error (possibly parsing)!")
        }
    }


    protected fun <T> get(observable: Observable<T>): Observable<T> = observable
            .subscribeOn(GL_SCHEDULER_IO)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(errorConsumer)
}
