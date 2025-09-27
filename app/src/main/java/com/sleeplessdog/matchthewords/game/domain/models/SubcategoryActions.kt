package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryActions(val key: String) {
    BASIC("basic"),
    COMMUNICATION("communication"),
    PERCEPTION("perception"),
    MENTAL("mental"),
    POSSESSION("possession"),
    CREATION("creation"),
    BODY_MOTION("body_motion"),
    EMOTION("emotion"),
    SOCIAL("social"),
    WORK_STUDY("work_study"),
    CHANGE("change"),
    MOVEMENT_TRANSPORT("movement_transport"),
    CONTROL("control"),
    EXPRESSION("expression"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryActions? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryActions =
            fromKey(key) ?: OTHER
    }
}
