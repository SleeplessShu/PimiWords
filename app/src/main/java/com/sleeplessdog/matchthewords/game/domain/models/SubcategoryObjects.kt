package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryObjects(val key: String) {
    HOUSEHOLD_COMMON("household_common"),
    KITCHEN_UTENSIL("kitchen_utensil"),
    TABLEWARE("tableware"),
    ELECTRONICS_GADGET("electronics_gadget"),
    SMALL_APPLIANCE("small_appliance"),
    OFFICE_STATIONERY("office_stationery"),
    SCHOOL_SUPPLIES("school_supplies"),
    TOOL_HAND("tool_hand"),
    TOOL_POWER("tool_power"),
    HARDWARE_FASTENER("hardware_fastener"),
    CONTAINER_STORAGE("container_storage"),
    LIGHTING_DEVICE("lighting_device"),
    MUSIC_INSTRUMENT("music_instrument"),
    TOY_GAME_SPORT("toy_game_sport"),
    HYGIENE_COSMETIC("hygiene_cosmetic"),
    MEDIA_ITEM("media_item"),
    PERSONAL_ACCESSORY("personal_accessory"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryObjects? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryObjects =
            fromKey(key) ?: OTHER
    }
}
