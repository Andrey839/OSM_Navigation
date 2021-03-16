package com.myapp.osmnavigation.netWork

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.myapp.osmnavigation.util.Const
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestApiOSM {

    @GET(Const.REVERSE_LOCATION_TO_PLACE)
    suspend fun getPlaceWithLocationAsync(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): ReverseLocationToPlace

    @GET(Const.SEARCH_PLACE)
    suspend fun getPlace(
        @Query("q") q: String,
        @Query("country") country: String
    ): List<ReverseLocationToPlace>

    @GET(Const.SEARCH_PLACE)
    suspend fun getPlaceAddress(
        @Query("q") q: String,
        @Query("addressdetails") addressdetails: String,
        @Query("country") country: String
    ): List<SearchAddress>

    // запрос для Flickr API
    @GET(Const.URL_FLICKR_PHOTO)
    suspend fun getPhotoFlickrPlace(
        @Query("method") method: String,
        @Query("api_key") api_key: String,
        @Query("lat") lat: String,
        @Query("lon") lon :String,
        @Query("page") page: String,
        @Query("format") format: String,
        @Query("nojsoncallback") nojsoncallback: String
    ): PhotosContainer

    // wikipedia
    @GET(Const.URL_WIKIPEDIA_SEARCH_JSON_LOCATION)
    suspend fun getWikipediaPlace(
        @Query("lang") lang: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("radius") radius: Int,
        @Query("username") username: String
    ): ContainerWikipedia
}


private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object Api {
    private val nearbyRequest = Retrofit.Builder()

        .baseUrl(Const.BASIC_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val getData: RequestApiOSM = nearbyRequest.create(RequestApiOSM::class.java)
}