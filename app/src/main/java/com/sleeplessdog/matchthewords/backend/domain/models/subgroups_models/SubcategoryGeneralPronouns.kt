package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryGeneralPronouns(val key: String) {
    PERSONAL_SUBJECT("personal_subject"),
    PERSONAL_OBJECT("personal_object"),
    POSSESSIVE_DETERMINER("possessive_determiner"),
    POSSESSIVE_PRONOUN("possessive_pronoun"),
    REFLEXIVE("reflexive"),
    DEMONSTRATIVE("demonstrative"),
    INTERROGATIVE("interrogative"),
    RELATIVE("relative"),
    INDEFINITE("indefinite"),
    QUANTIFIER("quantifier"),
    DISTRIBUTIVE("distributive"),
    EMPHATIC("emphatic"),
    RECIPROCAL("reciprocal"),
    FORMAL_OBSOLETE("formal_obsolete"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryGeneralPronouns? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryGeneralPronouns =
            fromKey(key) ?: OTHER
    }
}
