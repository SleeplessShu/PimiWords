package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryEmotions(val key: String) {
    BASIC_CORE("basic_core"),
    POSITIVE("positive"),
    NEGATIVE("negative"),
    SOCIAL_EMOTION("social_emotion"),
    ROMANTIC_ATTACHMENT("romantic_attachment"),
    MOOD_TEMPERAMENT("mood_temperament"),
    SELF_EVALUATIVE("self_evaluative"),
    AROUSAL_ACTIVATION("arousal_activation"),
    EXPRESSION_ACTION("expression_action"),
    INTENSITY_ADJ_POSITIVE("intensity_adj_positive"),
    INTENSITY_ADJ_NEGATIVE("intensity_adj_negative"),
    COPING_REGULATION("coping_regulation"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryEmotions? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryEmotions =
            fromKey(key) ?: OTHER
    }
}
