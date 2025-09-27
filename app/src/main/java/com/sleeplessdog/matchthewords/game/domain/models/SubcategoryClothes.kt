package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryClothes(val key: String) {
    TOPS("tops"),
    BOTTOMS("bottoms"),
    UNDERWEAR("underwear"),
    FOOTWEAR("footwear"),
    OUTERWEAR("outerwear"),
    HEADWEAR("headwear"),
    ACCESSORIES("accessories"),
    BAGS("bags"),
    JEWELRY("jewelry"),
    SPORTSWEAR("sportswear"),
    SLEEPWEAR("sleepwear"),
    SWIMWEAR("swimwear"),
    FORMALWEAR("formalwear"),
    WORKWEAR_UNIFORM("workwear_uniform"),
    FABRIC_MATERIAL("fabric_material"),
    PATTERN("pattern"),
    CLOSURE("closure"),
    SIZE_FIT("size_fit"),
    LAUNDRY_CARE("laundry_care"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryClothes? =
            key?.lowercase()?.let(byKey::get)
        fun parseOrOther(key: String?) =
            fromKey(key) ?: OTHER
    }
}
