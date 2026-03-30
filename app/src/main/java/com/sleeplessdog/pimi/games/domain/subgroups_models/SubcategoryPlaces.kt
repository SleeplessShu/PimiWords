package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryPlaces(val key: String) {
    GEOGRAPHIC_UNIT("geographic_unit"),
    ADDRESS_COMPONENT("address_component"),
    URBAN_AREA("urban_area"),
    NATURAL_LANDFORM("natural_landform"),
    WATERBODY("waterbody"),
    BUILDING_RESIDENTIAL("building_residential"),
    ACCOMMODATION("accommodation"),
    FOOD_VENUE("food_venue"),
    RETAIL("retail"),
    WORKPLACE_INDUSTRY("workplace_industry"),
    BUILDING_EDUCATION("building_education"),
    BUILDING_HEALTH("building_health"),
    BUILDING_RELIGIOUS("building_religious"),
    BUILDING_PUBLIC("building_public"),
    BUILDING_CULTURE("building_culture"),
    BUILDING_TRANSPORT("building_transport"),
    INFRASTRUCTURE_TRANSPORT("infrastructure_transport"),
    INFRASTRUCTURE_UTILITY("infrastructure_utility"),
    LEISURE_PARK("leisure_park"),
    TOURIST_ATTRACTION("tourist_attraction"),
    NATURE_PROTECTED("nature_protected"),
    RURAL_AREA("rural_area"),
    ADMINISTRATIVE_SECURITY("administrative_security"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryPlaces? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryPlaces =
            fromKey(key) ?: OTHER
    }
}
