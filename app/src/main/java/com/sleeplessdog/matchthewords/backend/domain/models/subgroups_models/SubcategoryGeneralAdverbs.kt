package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryGeneralAdverbs(val key: String) {
    TIME_WHEN("time_when"),
    TIME_SEQUENCE("time_sequence"),
    FREQUENCY("frequency"),
    DEGREE_INTENSITY("degree_intensity"),
    MANNER_GENERAL("manner_general"),
    PLACE_LOCATION("place_location"),
    DIRECTION_MOVEMENT("direction_movement"),
    CERTAINTY_EPISTEMIC("certainty_epistemic"),
    ATTITUDE_EVALUATIVE("attitude_evaluative"),
    DISCOURSE_ADDITION("discourse_addition"),
    DISCOURSE_CONTRAST("discourse_contrast"),
    DISCOURSE_CAUSE_EFFECT("discourse_cause_effect"),
    DISCOURSE_SEQUENCE("discourse_sequence"),
    FOCUS_LIMITER("focus_limiter"),
    NEGATION_POLARITY("negation_polarity"),
    INTERROGATIVE("interrogative"),
    COMPARATIVE_MODIFIERS("comparative_modifiers"),
    ASPECTUAL("aspectual"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryGeneralAdverbs? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryGeneralAdverbs =
            fromKey(key) ?: OTHER
    }
}
