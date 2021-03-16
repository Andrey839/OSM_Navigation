package com.myapp.osmnavigation.mapOSM

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myapp.osmnavigation.repository.RepositoryNetWork
import com.myapp.osmnavigation.util.Const
import kotlinx.coroutines.delay
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.routing.Road

class MapViewModel : ViewModel() {

    val pois = MutableLiveData<ArrayList<POI>>()
    val poisFlickr = MutableLiveData<ArrayList<POI>>()
    val poisWikipedia = MutableLiveData<ArrayList<POI>>()
    private val repository = RepositoryNetWork()

    // находим все достопримечательности вдоль маршрута
    suspend fun poisRoad(poiProvader: NominatimPOIProvider, road: Road) {
        for (poi in Const.LIST_POIS_TYPES) {
            pois.postValue(poiProvader.getPOIAlong(road.routeLow, poi, 50, 5.0))
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
                Const.GET_IMAGE_FLICKR + infImage?.photos?.photo?.first()?.server + "/" +
                        infImage?.photos?.photo?.first()?.id + "_" +
                        infImage?.photos?.photo?.first()?.secret + ".jpg"
        }
        poisFlickr.postValue(listPOI)
    }

    suspend fun wikipediaPlaces(listPOI: ArrayList<POI>) {
        for (poi in listPOI) {
            val wikiInf = repository.wikipediaPlaceInfo(
                lat = poi.mLocation.latitude.toString(),
                lng = poi.mLocation.longitude.toString()
            )
            Log.e("tyi","wiki ${if(wikiInf?.geonames?.isNotEmpty() == true) wikiInf.geonames.first().summary else ""}")
            if (wikiInf?.geonames?.isNotEmpty() == true) {
                poi.mThumbnailPath = wikiInf.geonames.first().thumbnailImg
                poi.mCategory = wikiInf.geonames.first().title
                poi.mDescription = wikiInf.geonames.first().summary
            }
        }
        poisWikipedia.postValue(listPOI)
    }
}