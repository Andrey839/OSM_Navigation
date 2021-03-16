package com.myapp.osmnavigation.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myapp.osmnavigation.repository.RepositoryNetWork
import com.myapp.osmnavigation.util.Const
import kotlinx.coroutines.delay
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.util.BoundingBox

class SearchPlacesViewModel : ViewModel() {

    val repository = RepositoryNetWork()

    val pois = MutableLiveData<ArrayList<POI>>()
    val poisFlickr = MutableLiveData<ArrayList<POI>>()
    val poisGeoNames = MutableLiveData<ArrayList<POI>>()

    // находим все достопримечательности вдоль маршрута
    suspend fun poisRoad(poiProvader: NominatimPOIProvider, road: Road?) {
        for (poi in Const.LIST_POIS_TYPES) {
            pois.postValue(poiProvader.getPOIAlong(road?.routeLow, poi, 50, 5.0))
            delay(1050)
        }
    }

    // фотографии вдоль маршрута
    suspend fun poisFlickr(listPOI: ArrayList<POI>, apiKey: String) {
        for (poi in listPOI) {
            val infImage = repository.informPhoto(
                apiKey,
                poi.mLocation.latitude.toString(),
                poi.mLocation.longitude.toString()
            )
            poi.mThumbnailPath =
                Const.GET_IMAGE_FLICKR + infImage?.photos?.photo?.get(1)?.server + "/" +
                        infImage?.photos?.photo?.get(1)?.id + "_" +
                        infImage?.photos?.photo?.get(1)?.secret + ".jpg"
        }
        poisFlickr.postValue(listPOI)
    }

    fun poisGeoNames(poiGeoNames: GeoNamesPOIProvider, bb: BoundingBox) {
        poisGeoNames.postValue(poiGeoNames.getPOIInside(bb, 30))
    }
}