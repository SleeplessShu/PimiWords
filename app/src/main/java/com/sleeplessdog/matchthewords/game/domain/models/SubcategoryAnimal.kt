package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryAnimal(val key: String) {
    MAMMAL("mammal"),
    MARINE_MAMMAL("marine_mammal"),
    BIRD("bird"),
    REPTILE("reptile"),
    AMPHIBIAN("amphibian"),
    FISH_FRESHWATER("fish_freshwater"),
    FISH_MARINE("fish_marine"),
    INSECT("insect"),
    ARACHNID("arachnid"),
    CRUSTACEAN("crustacean"),
    MOLLUSK("mollusk"),
    ECHINODERM("echinoderm"),
    ANNELID("annelid"),
    CNIDARIAN("cnidarian"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryAnimal? =
            key?.lowercase()?.let(byKey::get)
    }
}