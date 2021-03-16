package com.myapp.osmnavigation.netWork

import com.myapp.osmnavigation.util.InfoPlacesList
import com.squareup.moshi.JsonClass
import java.sql.Struct

// разьиваем ответ по локации
@JsonClass(generateAdapter = true)
data class ReverseLocationToPlace(
    val features: List<Items> = listOf()
)

@JsonClass(generateAdapter = true)
data class Items(
    val properties: Properties? = null,
    val geometry: Geometry
)

@JsonClass(generateAdapter = true)
data class Geometry(
    val coordinates: List<Double>
)

@JsonClass(generateAdapter = true)
data class Properties(
    val place_id: Int,
//    @Json(name = "name city") val class: String,
    val display_name: String,
    val icon: String = "",
    val category: String,
    val type: String,
    val address: Address? = null
)

@JsonClass(generateAdapter = true)
data class Address(
    val house_number: String = "",
    val road: String = "",
    val city: String = "",
    val state: String = "",
    val postcode: String = "",
    val country: String = "",
    val country_code: String = ""
)

@JsonClass(generateAdapter = true)
data class SearchAddress(
    val address: Address,
    val boundingbox: List<String>,
    val display_name: String,
    val icon: String = "",
    val place_id: String,
    val type: String
)

fun List<ReverseLocationToPlace>.toInfoPlaces(): List<InfoPlacesList> {
    return map {
        InfoPlacesList(
            name = it.features.first().properties?.display_name ?: "",
            icon = it.features.first().properties?.icon ?: "",
            lat = it.features.first().geometry.coordinates.first().toString(),
            lon = it.features.first().geometry.coordinates.last().toString()
        )
    }

}

fun List<SearchAddress>.toInfoAddress(): List<InfoPlacesList> {
    return map {
        InfoPlacesList(
            name = it.display_name ?: "",
            icon = it.icon ?: "",
            lat = it.boundingbox.first(),
            lon = it.boundingbox.last()
        )
    }
}

// парсим Json
// для Flickr
@JsonClass(generateAdapter = true)
data class PhotosContainer(
    val photos: ListPhotos
)

@JsonClass(generateAdapter = true)
data class ListPhotos(
    val photo: List<DetailPhoto>
)
@JsonClass(generateAdapter = true)
data class DetailPhoto (
    val id: String,
    val secret: String,
    val server: String,
    val title: String
        )
@JsonClass(generateAdapter = true)
data class ContainerWikipedia(
    val geonames: List<WikipediaPlace>
)

@JsonClass(generateAdapter = true)
data class WikipediaPlace(
    val summary: String = "",
    val elevation: Int = 0,
    val geoNameId: Int = 0,
    val feature: String = "",
    val rank: Int = 0,
    val thumbnailImg: String = "",
    val title: String = "",
    val wikipediaUrl: String = ""
)

