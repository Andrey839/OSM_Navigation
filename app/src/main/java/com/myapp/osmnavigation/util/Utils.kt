package com.myapp.osmnavigation.util

data class InfoPlacesList(
    val name: String,
    val icon: String = "",
    val lat: String,
    val lon: String
)

enum class NameTypes(val s: String) {
    Аэропорт("Airport"),
    Банк("Bank"),
    Бар("Bar"),
    Станция("Station"),
    Кафе("Cafe"),
    Казино("Casino"),
    Кинотеатр("Cinema"),
    Общественный_центр("Community Centre"),
    Велопарковка ("Cycle parking"),
    Fast_food("Fast food"),
    Больница("Hospital"),
    Торговая_площадка ("Marketplace"),
    Ночной_клуб ("Night club"),
    Полиция ("Police"),
    Паб ("Pub"),
    Рестаран ("Restaurant"),
    Театр ("Theatre"),
    Ратуша("Town Hall"),
    Церковь ("Church"),
    Отель ("Hotel"),
    Замок ("Castle"),
    Монумент ("Monument"),
    Парк ("Park"),
    Стадион ("Stadium"),
    Пляж ("Beach"),
    Атракцион ("Attraction"),
    Кемпинг ("Camp site"),
    Музей ("Museum"),
    Тематицеский_парк ("Theme park"),
    Аквапарк ("Water park"),
    Зоопарк ("Zoo")
}