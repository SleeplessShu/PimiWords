package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryMiscellaneous(val key: String) {
    ARTICLES("articles"),
    PREPOSITIONS_BASIC("prepositions_basic"),
    PREPOSITIONS_ADVANCED("prepositions_advanced"),
    CONJUNCTIONS_COORD("conjunctions_coord"),
    CONJUNCTIONS_SUBORD("conjunctions_subord"),
    INTERJECTIONS("interjections"),
    DISCOURSE_MARKERS("discourse_markers"),
    MODAL_AUXILIARIES("modal_auxiliaries"),
    AUXILIARY_VERBS("auxiliary_verbs"),
    PARTICLES_PHRASAL("particles_phrasal"),
    POLITENESS_FORMULAS("politeness_formulas"),
    RESPONSES_ACK("responses_ack"),
    ABBREVIATIONS_ACRONYMS("abbreviations_acronyms"),
    SYMBOLS_UNITS("symbols_units"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryMiscellaneous? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryMiscellaneous =
            fromKey(key) ?: OTHER
    }
}
