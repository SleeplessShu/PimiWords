package com.sleeplessdog.matchthewords.backend.domain.models.subgroups_models

enum class SubcategoryHealth(val key: String) {
    ANATOMY_BASIC("anatomy_basic"),
    ANATOMY_SYSTEMS("anatomy_systems"),
    SYMPTOM_SIGN("symptom_sign"),
    DISEASE_COMMON("disease_common"),
    DISEASE_INFECTIOUS("disease_infectious"),
    DISEASE_SPECIAL("disease_special"),
    MENTAL_HEALTH("mental_health"),
    DIAGNOSTICS_TESTS("diagnostics_tests"),
    PROCEDURES_SURGERY("procedures_surgery"),
    MEDICATION_PHARMA("medication_pharma"),
    NUTRITION_DIET("nutrition_diet"),
    LIFESTYLE_PREVENTION("lifestyle_prevention"),
    REHAB_THERAPY("rehab_therapy"),
    DEVICES_SUPPLIES("devices_supplies"),
    HEALTHCARE_ROLES("healthcare_roles"),
    FACILITIES_CARE("facilities_care"),
    INSURANCE_ADMIN("insurance_admin"),
    EMERGENCY_FIRSTAID("emergency_firstaid"),
    METRICS_UNITS("metrics_units"),
    PUBLIC_HEALTH("public_health"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryHealth? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryHealth =
            fromKey(key) ?: OTHER
    }
}
