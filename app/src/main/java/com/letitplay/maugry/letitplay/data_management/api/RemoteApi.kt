package com.letitplay.maugry.letitplay.data_management.api


import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.*
import com.letitplay.maugry.letitplay.BuildConfig
import com.letitplay.maugry.letitplay.GL_DATA_SERVICE_URL
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.api.responses.*
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Response
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
         .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().header("Authorization", ServiceLocator.preferenceHelper.userJwt)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

class SearchResponseDeserializer : JsonDeserializer<SearchResponseItem> {
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

object UnitConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, *>? {

        val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)
        return object : Converter<ResponseBody, Any> {
            override fun convert(value: ResponseBody): Any? {
                return when (value.contentLength().toInt() == 0) {
                    true -> null
                    else -> delegate.convert(value)
                }
            }
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
        .addConverterFactory(UnitConverterFactory)
        .addConverterFactory(GsonConverterFactory.create(gson))

val serviceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayApi::class.java)

val postServiceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayPostApi::class.java)

val putServiceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayPutApi::class.java)

val deleteServiceImpl = serviceBuilder
        .baseUrl(GL_DATA_SERVICE_URL)
        .build()
        .create(LetItPlayDeleteApi::class.java)

interface LetItPlayApi {

    @GET("stations")
    fun channels(): Single<List<Channel>>

    @GET("stations/{id}/tracks")
    fun getChannelTracks(@Path("id") idStation: Int): Single<List<TrackWithEmbeddedChannel>>

    @GET("tracks")
    fun getTracks(): Single<List<Track>>

    @GET("tracks/{id}")
    fun getTrackPiece(@Path("id") trackId: Int): Single<TrackWithEmbeddedChannel>

    @GET("feed?")
    fun getFeed(
            @Query("stIds") stIds: String,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int,
            @Query("lang") lang: String
    ): Single<List<TrackWithEmbeddedChannel>>

    @GET("abrakadabra?")
    fun getCompilation(@Query("lang") lang: String): Single<TracksAndChannels>

    @GET("trends?")
    fun getTrends(
            @Query("offset") offset: Int,
            @Query("limit") limit: Int,
            @Query("lang") lang: String
    ): Single<List<TrackWithEmbeddedChannel>>

    @GET("stations/{id}")
    fun getChannelPiece(@Path("id") channelId: Int): Single<Channel>

    @GET("search")
    fun search(@Query("q") query: String): Single<SearchResponse>
}


interface LetItPlayPostApi {
    @POST("stations/{id}/counts/")
    fun updateChannelFollowers(@Path("id") idStation: Int, @Body followers: UpdateFollowersRequestBody): Single<UpdatedChannelResponse>

    @POST("tracks/{id}/counts/")
    fun updateFavouriteTracks(@Path("id") idTrack: Int, @Body likes: UpdateRequestBody): Single<UpdatedTrackResponse>

    @POST("/auth/signup")
    fun signup(@Query("uid") uid: String,
               @Query("username") username: String): Single<Response<Any>>

    @POST("/auth/signin")
    fun signin(@Query("uid") uid: String,
               @Query("username") username: String): Single<Response<Any>>
}

interface LetItPlayPutApi {

    @PUT("follow/channel/{id}")
    fun updateChannelFollowers(@Path("id") idStation: Int): Single<Channel>

    @PUT("like/{id}")
    fun updateFavouriteTracks(@Path("id") idTrack: Int): Single<TrackWithEmbeddedChannel>
}

interface LetItPlayDeleteApi {

    @DELETE("follow/channel/{id}")
    fun unFollowChannel(@Path("id") idStation: Int): Single<Channel>

    @DELETE("like/{id}")
    fun unLikeTracks(@Path("id") idTrack: Int): Single<TrackWithEmbeddedChannel>

}