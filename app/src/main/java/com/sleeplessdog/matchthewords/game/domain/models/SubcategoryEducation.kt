package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryEducation(val key: String) {
    INSTITUTION("institution"),
    PLACE_ON_CAMPUS("place_on_campus"),
    PEOPLE_ROLE("people_role"),
    SUBJECT("subject"),
    DIPLOMA_DEGREE("diploma_degree"),
    ACADEMIC_LEVEL("academic_level"),
    ACTIVITY_PROCESS("activity_process"),
    ASSESSMENT("assessment"),
    MATERIALS_RESOURCE("materials_resource"),
    TECHNOLOGY_TOOL("technology_tool"),
    SCHEDULE_CALENDAR("schedule_calendar"),
    ADMINISTRATION_POLICY("administration_policy"),
    EXTRACURRICULAR("extracurricular"),
    VERB_ACTION("verb_action"),
    RESEARCH_ACADEMIA("research_academia"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryEducation? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryEducation =
            fromKey(key) ?: OTHER
    }
}
