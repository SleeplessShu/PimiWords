package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryFamily(val key: String) {
    RELATIVES_GENERAL("relatives_general"),
    SPOUSE_PARTNER("spouse_partner"),
    PARENTHOOD("parenthood"),
    CHILDHOOD("childhood"),
    SIBLING("sibling"),
    GRANDPARENT("grandparent"),
    GRANDCHILD("grandchild"),
    AUNT_UNCLE("aunt_uncle"),
    COUSIN("cousin"),
    IN_LAW("in_law"),
    STEP_FAMILY("step_family"),
    ADOPT_FOSTER_GUARDIAN("adopt_foster_guardian"),
    MARRIAGE_EVENTS("marriage_events"),
    HOUSEHOLD_ROLES("household_roles"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryFamily? =
            key?.lowercase()?.let(byKey::get)
        fun parseOrOther(key: String?) =
            fromKey(key) ?: OTHER
    }
}
