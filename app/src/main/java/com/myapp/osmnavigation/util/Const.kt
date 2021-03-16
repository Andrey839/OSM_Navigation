package com.myapp.osmnavigation.util

class Const {

    companion object {
        const val PERMISSION_NUMB = 34
        const val KEY_LAT_LNG = "lat_lng"
        const val SET_ACTION = "receive"
        const val BASIC_URL = "https://nominatim.openstreetmap.org/"
        const val REVERSE_LOCATION_TO_PLACE = "reverse?format=geojson"
        const val SEARCH_PLACE = "search?format=json"
        const val ADDRESS_DETAIL = "1"
        const val URL_WIKIPEDIA_SEARCH_JSON_LOCATION = "http://api.geonames.org/findNearbyWikipediaJSON?"
        const val LANG = "ru"
        const val RADIUS = 1
        const val MAX_ROWS = 2
        const val USER_NAME = "andi839"
        const val URL_FLICKR_PHOTO =
            "https://www.flickr.com/services/rest/?"
        const val PHOTO_SEARCH = "flickr.photos.search"
        const val GET_IMAGE_FLICKR = "https://live.staticflickr.com/"
        const val FORMAT_CALLBACK = "json"
        const val PERMISSION_NUMBER_WRITE_STORAGE = 45
        const val PERMISSION_NUMBER_READ_STORAGE = 54
        val LIST_POIS_TYPES = listOf(
            "Airport",
            "Bank",
            "Bar",
            "station",
            "Cafe",
            "Casino",
            "Cinema",
            "Community Centre",
            "Cycle parking",
            "Fast food",
            "Hospital",
            "Marketplace",
            "Night club",
            "Police",
            "Pub",
            "Recycling point",
            "Restaurant",
            "Retirement home",
            "Shelter",
            "Theatre",
            "Town Hall",
            "Church",
            "Hotel",
            "Castle",
            "Monument",
            "Park",
            "Stadium",
            "Beach",
            "Attraction",
            "Camp site",
            "Museum",
            "Theme park",
            "Water park",
            "Zoo"
        )
    }

}