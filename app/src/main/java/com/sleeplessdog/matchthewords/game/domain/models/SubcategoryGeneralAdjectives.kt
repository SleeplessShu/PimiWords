package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryGeneralAdjectives(val key: String) {
    SIZE("size"),
    SHAPE("shape"),
    COLOR_TONE("color_tone"),
    QUALITY("quality"),
    AGE_TIME("age_time"),
    QUANTITY_DEGREE("quantity_degree"),
    TEMPERATURE("temperature"),
    EMOTION_MOOD("emotion_mood"),
    PERSONALITY_TRAIT("personality_trait"),
    SPEED_INTENSITY("speed_intensity"),
    VALUE_IMPORTANCE("value_importance"),
    APPEARANCE("appearance"),
    SOUND("sound"),
    TASTE_SMELL("taste_smell"),
    DIFFICULTY("difficulty"),
    CONDITION_STATE("condition_state"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryGeneralAdjectives? =
            key?.lowercase()?.let(byKey::get)
        fun parseOrOther(key: String?): SubcategoryGeneralAdjectives =
            fromKey(key) ?: OTHER
    }
}
