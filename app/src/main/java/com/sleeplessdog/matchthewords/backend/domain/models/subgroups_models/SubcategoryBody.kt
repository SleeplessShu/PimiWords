package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryBody(val key: String) {
    EXTERNAL_BASIC("external_basic"),
    FACIAL_FEATURES("facial_features"),
    TORSO("torso"),
    LIMBS("limbs"),
    INTERNAL_ORGANS("internal_organs"),
    SYSTEMS("systems"),
    MUSCLES_TISSUES("muscles_tissues"),
    FLUIDS("fluids"),
    MOVEMENT_POSTURE("movement_posture"),
    SENSES("senses"),
    METRICS("metrics"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryBody? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryBody =
            fromKey(key) ?: OTHER
    }
}
