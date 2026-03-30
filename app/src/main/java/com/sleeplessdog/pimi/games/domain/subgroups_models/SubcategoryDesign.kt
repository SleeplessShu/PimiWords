package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryDesign(val key: String) {
    BASICS("basics"),
    NAVIGATION_IA("navigation_ia"),
    FORMS_INPUTS("forms_inputs"),
    COMPONENTS("components"),
    LAYOUT_GRID_RESPONSIVE("layout_grid_responsive"),
    TYPOGRAPHY("typography"),
    COLOR_THEME("color_theme"),
    ICONOGRAPHY_IMAGERY("iconography_imagery"),
    MOTION_STATES("motion_states"),
    CONTENT_MICROCOPY("content_microcopy"),
    ACCESSIBILITY("accessibility"),
    RESEARCH_METRICS("research_metrics"),
    WIREFRAME_PROTOTYPE("wireframe_prototype"),
    DESIGN_SYSTEMS_TOKENS("design_systems_tokens"),
    PROCESS_HANDOFF("process_handoff"),
    TOOLS("tools"),
    HEURISTICS_PATTERNS("heuristics_patterns"),
    PRODUCT_ANALYTICS("product_analytics"),
    LOCALIZATION_INTERNATIONAL("localization_international"),
    NOTIFICATIONS_PERMISSIONS("notifications_permissions"),
    MONETIZATION_PAYMENTS("monetization_payments"),
    USABILITY_ISSUES("usability_issues"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryDesign? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryDesign =
            fromKey(key) ?: OTHER
    }
}
