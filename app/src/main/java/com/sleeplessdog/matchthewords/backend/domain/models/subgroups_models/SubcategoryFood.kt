package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryFood(val key: String) {
    APPLIANCE("appliance"),
    COOKING_METHOD("cooking_method"),
    DAIRY("dairy"),
    DISH("dish"),
    DRINK("drink"),
    FRUIT("fruit"),
    GRAIN("grain"),
    MEASURE("measure"),
    MEAT("meat"),
    OTHER("other"),
    PACKAGING("packaging"),
    RESTAURANT("restaurant"),
    SAUCE("sauce"),
    SEAFOOD("seafood"),
    SPICE("spice"),
    SWEET("sweet"),
    TASTE("taste"),
    UTENSIL("utensil"),
    VEGETABLE("vegetable");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryFood? =
            key?.lowercase()?.let(byKey::get)
    }
}