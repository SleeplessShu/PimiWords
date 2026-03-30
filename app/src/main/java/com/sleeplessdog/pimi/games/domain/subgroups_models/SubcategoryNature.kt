package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryNature(val key: String) {
    LANDFORM("landform"),
    WATERBODY("waterbody"),
    FLORA_PLANT("flora_plant"),
    BIOME_ECOSYSTEM("biome_ecosystem"),
    WEATHER("weather"),
    CLIMATE("climate"),
    CELESTIAL("celestial"),
    NATURAL_MATERIAL("natural_material"),
    NATURAL_EVENT("natural_event"),
    ENVIRONMENT_CONSERVATION("environment_conservation"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }

        fun fromKey(key: String?): SubcategoryNature? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryNature =
            fromKey(key) ?: OTHER
    }
}
