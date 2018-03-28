package com.letitplay.maugry.letitplay.data_management.api


import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.*
import com.letitplay.maugry.letitplay.BuildConfig
import com.letitplay.maugry.letitplay.GL_DATA_SERVICE_URL
import com.letitplay.maugry.letitplay.GL_POST_REQUEST_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.api.responses.*
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

private val logInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
}

private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .build()

class SearchResponseDeserializer: JsonDeserializer<SearchResponseItem> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SearchResponseItem {
        return try {
            val audioUrl = json!!["AudioURL"].asString
            val track = gson.fromJson(json, Track::class.java)
            val channel = gson.fromJson<Channel>(json["station"])
            SearchResponseItem.TrackSearchResponse(TrackWithChannel(track, channel, null))
        } catch (e: NoSuchElementException) {
            SearchResponseItem.ChannelSearchResponse(gson.fromJson(json, Channel::class.java))
        }
    }
}


private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .registerTypeAdapter(SearchResponseItem::class.java, SearchResponseDeserializer())
        .create()

private val serviceBuilder = Retrofit.Builder()
        .client(httpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))

val serviceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayApi::class.java)

val postServiceImpl = serviceBuilder
        .baseUrl(GL_POST_REQUEST_SERVICE_URL)
        .build()
        .create(LetItPlayPostApi::class.java)

interface LetItPlayApi {

    @GET("stations")
    fun channels(): Single<List<Channel>>

    @GET("stations/{id}/tracks")
    fun getChannelTracks(@Path("id") idStation: Int): Single<List<Track>>

    @GET("tracks")
    fun getTracks(): Single<List<Track>>

    @GET("feed?")
    fun getFeed(
            @Query("stIds") stIds: String,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int,
            @Query("lang") lang: String
    ): Single<List<FeedResponseItem>>

    @GET("abrakadabra?")
    fun getCompilation(@Query("lang") lang: String): Single<TracksAndChannels>

    @GET("trends?")
    fun getTrends(
            @Query("offset") offset: Int,
            @Query("limit") limit: Int,
            @Query("lang") lang: String
    ): Single<List<FeedResponseItem>>

    @GET("stations/{id}")
    fun getChannelPiece(@Path("id") channelId: Int): Maybe<Channel>

    @GET("search")
    fun search(@Query("q") query: String): Single<SearchResponse>
}


interface LetItPlayPostApi {
    @POST("stations/{id}/counts/")
    fun updateChannelFollowers(@Path("id") idStation: Int, @Body followers: UpdateFollowersRequestBody): Single<UpdatedChannelResponse>

    @POST("tracks/{id}/counts/")
    fun updateFavouriteTracks(@Path("id") idTrack: Int, @Body likes: UpdateRequestBody): Single<UpdatedTrackResponse>
}