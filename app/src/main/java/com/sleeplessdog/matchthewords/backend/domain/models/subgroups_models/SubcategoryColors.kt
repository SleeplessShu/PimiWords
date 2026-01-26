package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryColors(val key: String) {
    BASIC("basic"),
    LIGHT_DARK("light_dark"),
    SHADES_RED("shades_red"),
    SHADES_BLUE("shades_blue"),
    SHADES_GREEN("shades_green"),
    SHADES_YELLOW_ORANGE("shades_yellow_orange"),
    SHADES_PURPLE_PINK("shades_purple_pink"),
    SHADES_BROWN("shades_brown"),
    SHADES_GRAY("shades_gray"),
    PATTERNS("patterns"),
    METALLIC("metallic"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryColors? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryColors =
            fromKey(key) ?: OTHER
    }
}
