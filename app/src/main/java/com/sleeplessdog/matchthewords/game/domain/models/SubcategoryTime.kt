package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryTime(val key: String) {
    UNIT("unit"),
    PART_OF_DAY("part_of_day"),
    DAY("day"),
    MONTH("month"),
    SEASON("season"),
    HOLIDAY("holiday"),
    FREQUENCY("frequency"),
    CALENDAR_TERM("calendar_term"),
    TEMPORAL_EXPRESSION("temporal_expression"),
    HISTORICAL_PERIOD("historical_period"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }

        fun fromKey(key: String?): SubcategoryTime? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryTime =
            fromKey(key) ?: OTHER
    }
}
