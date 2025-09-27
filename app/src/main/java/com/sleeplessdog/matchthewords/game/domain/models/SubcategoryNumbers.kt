package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryNumbers(val key: String) {
    CARDINAL("cardinal"),
    ORDINAL("ordinal"),
    FRACTION("fraction"),
    DECIMAL("decimal"),
    ROMAN("roman"),
    MEASURE_NUMBER("measure_number"),
    MATH_TERM("math_term"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryNumbers? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryNumbers =
            fromKey(key) ?: OTHER
    }
}
