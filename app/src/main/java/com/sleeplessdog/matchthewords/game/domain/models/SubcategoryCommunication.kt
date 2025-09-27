package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryCommunication(val key: String) {
    BASIC("basic"),
    WRITTEN("written"),
    MEDIA_MASS("media_mass"),
    DIGITAL_ONLINE("digital_online"),
    NONVERBAL("nonverbal"),
    RHETORIC_FORMAL("rhetoric_formal"),
    LITERARY("literary"),
    TECHNOLOGY_TERMS("technology_terms"),
    INTERPERSONAL("interpersonal"),
    ACADEMIC_SCIENTIFIC("academic_scientific"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryCommunication? =
            key?.lowercase()?.let(byKey::get)
        fun parseOrOther(key: String?): SubcategoryCommunication =
            fromKey(key) ?: OTHER
    }
}
