package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryTransport(val key: String) {
    VEHICLE_LAND("vehicle_land"),
    VEHICLE_AIR("vehicle_air"),
    VEHICLE_WATER("vehicle_water"),
    VEHICLE_RAIL("vehicle_rail"),
    VEHICLE_SPECIAL("vehicle_special"),
    PART_ENGINE("part_engine"),
    PART_BODY("part_body"),
    PART_INTERIOR("part_interior"),
    PART_CHASSIS("part_chassis"),
    PART_ACCESSORY("part_accessory"),
    FUEL_ENERGY("fuel_energy"),
    INFRASTRUCTURE("infrastructure"),
    ACTION("action"),
    DOCUMENT("document"),
    SAFETY("safety"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryTransport? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryTransport =
            fromKey(key) ?: OTHER
    }
}
