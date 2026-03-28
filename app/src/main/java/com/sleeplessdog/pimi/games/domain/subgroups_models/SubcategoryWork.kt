package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryWork(val key: String) {
    PROFESSION_ROLE("profession_role"),
    EMPLOYMENT_TYPE("employment_type"),
    WORKPLACE("workplace"),
    DEPARTMENT("department"),
    SCHEDULE_TIME("schedule_time"),
    RECRUITMENT("recruitment"),
    PROJECT_PROCESS("project_process"),
    COMMUNICATION("communication"),
    TOOLS_SOFTWARE("tools_software"),
    WORKPLACE_SAFETY("workplace_safety"),
    COMPENSATION_BENEFITS("compensation_benefits"),
    LEGAL_COMPLIANCE("legal_compliance"),
    ACTION_TASK("action_task"),
    BUSINESS_FINANCE("business_finance"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryWork? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryWork =
            fromKey(key) ?: OTHER
    }
}
