package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryHouse(val key: String) {
    ROOM("room"),
    FURNITURE("furniture"),
    APPLIANCE("appliance"),
    FIXTURE("fixture"),
    DECORATION("decoration"),
    GARDEN("garden"),
    TOOL("tool"),
    CLEANING("cleaning"),
    SECURITY("security"),
    BUILDING_MATERIAL("building_material"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryHouse? =
            key?.lowercase()?.let(byKey::get)
        fun parseOrOther(key: String?): SubcategoryHouse =
            fromKey(key) ?: OTHER
    }
}
