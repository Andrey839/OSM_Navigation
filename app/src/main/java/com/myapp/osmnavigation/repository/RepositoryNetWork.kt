package com.myapp.osmnavigation.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.myapp.osmnavigation.netWork.Api
import com.myapp.osmnavigation.netWork.ReverseLocationToPlace
import com.myapp.osmnavigation.netWork.SearchAddress
import com.myapp.osmnavigation.util.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryNetWork {

    val userPlace = MutableLiveData<ReverseLocationToPlace>()

    //поиск места по локации
    suspend fun requestWithLocation(locationUser: Location) {
        withContext(Dispatchers.IO) {
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
    }

    // поиск места по запросу
    suspend fun requestPlaces(place: String, country: String): List<ReverseLocationToPlace> {
        var listPlaces = listOf<ReverseLocationToPlace>()
        withContext(Dispatchers.IO) {
            try {
                val searchElement = Api.getData.getPlace(place, country)
                listPlaces = searchElement
                Log.e("tyi","search $searchElement")
            } catch (e: Exception) {
                Log.e("tyi", "error request $e")
            }
        }
        return listPlaces
    }

    // поиск адреса
    suspend fun requestAddress(place: String, country: String): List<SearchAddress> {
        var listAddress = listOf<SearchAddress>()
        withContext(Dispatchers.IO) {
            try {
                val searchAddress =
                    Api.getData.getPlaceAddress(place, Const.ADDRESS_DETAIL, country)
                listAddress = searchAddress
                Log.e("tyi","search $listAddress")
            } catch (e: Exception) {
                Log.e("tyi", "error search $e")
            }
        }
        return listAddress
    }
}