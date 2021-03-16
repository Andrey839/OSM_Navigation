package com.myapp.osmnavigation.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.myapp.osmnavigation.netWork.*
import com.myapp.osmnavigation.util.Const

class RepositoryNetWork {

    val userPlace = MutableLiveData<ReverseLocationToPlace>()

    //поиск места по локации
    suspend fun requestWithLocation(locationUser: Location) {
        try {
            val response =
                Api.getData.getPlaceWithLocationAsync(
                    locationUser.latitude.toString(),
                    locationUser.longitude.toString()
                )
            userPlace.postValue(response)
            Log.e(
                "tyi", "user place ${
                    Api.getData.getPlaceWithLocationAsync(
                        locationUser.latitude.toString(),
                        locationUser.longitude.toString()
                    )
                }"
            )
        } catch (e: Exception) {
            Log.e("tyi", "error requestWithLocation $e")
        }
    }

    // поиск места по запросу
    suspend fun requestPlaces(place: String, country: String): List<ReverseLocationToPlace> {
        var listPlaces = listOf<ReverseLocationToPlace>()
        try {
            val searchElement = Api.getData.getPlace(place, country)
            listPlaces = searchElement
            Log.e("tyi", "search $searchElement")
        } catch (e: Exception) {
            Log.e("tyi", "error request $e")
        }
        return listPlaces
    }

    // поиск адреса
    suspend fun requestAddress(place: String, country: String): List<SearchAddress> {
        var listAddress = listOf<SearchAddress>()
        try {
            val searchAddress =
                Api.getData.getPlaceAddress(place, Const.ADDRESS_DETAIL, country)
            listAddress = searchAddress
            Log.e("tyi", "search $listAddress")
        } catch (e: Exception) {
            Log.e("tyi", "error search $e")
        }
        return listAddress
    }

    // информация о фото
    suspend fun informPhoto(
        apiKey: String,
        lat: String,
        lon: String,
        page: String = 1.toString(),
        format: String = Const.FORMAT_CALLBACK,
        nojsoncallback: String = 1.toString()
    ): PhotosContainer? {
        var infPhoto: PhotosContainer? = null
        try {
            val requestInfPhoto = Api.getData.getPhotoFlickrPlace(
                Const.PHOTO_SEARCH,
                apiKey,
                lat,
                lon,
                page,
                format,
                nojsoncallback
            )
            infPhoto = requestInfPhoto
        } catch (e: Exception) {
            Log.e("tyi", "error request photo flickr $e")
        }
        return infPhoto
    }

    // информация с wikipedia
    suspend fun wikipediaPlaceInfo(
        lang: String = Const.LANG,
        lat: String,
        lng: String,
        radius: Int = Const.RADIUS / 5,
        userName: String = Const.USER_NAME
    ): ContainerWikipedia? {
        var listWikipedia: ContainerWikipedia? = null
        try {
            val requestWikipedia = Api.getData.getWikipediaPlace(lang,lat,lng,radius,userName)
            listWikipedia = requestWikipedia
        }catch (e: Exception){
            Log.e("tyi","error wikipedia $e")
        }
        return listWikipedia
    }
}