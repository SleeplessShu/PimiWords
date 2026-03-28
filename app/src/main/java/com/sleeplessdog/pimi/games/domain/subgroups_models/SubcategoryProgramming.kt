package com.sleeplessdog.pimi.games.domain.subgroups_models

enum class SubcategoryProgramming(val key: String) {
    BASICS("basics"),
    PARADIGMS("paradigms"),
    SYNTAX_TOKENS("syntax_tokens"),
    TYPES_GENERICS("types_generics"),
    CONTROL_FLOW("control_flow"),
    PROCEDURES_FUNCTIONS("procedures_functions"),
    OOP("oop"),
    DATA_STRUCTURES("data_structures"),
    ALGORITHMS("algorithms"),
    COMPLEXITY("complexity"),
    STORAGE_FORMATS("storage_formats"),
    IO_NETWORK("io_network"),
    WEB_FRONTEND("web_frontend"),
    BACKEND_API("backend_api"),
    DATABASES("databases"),
    CACHING_MQ_STREAMS("caching_mq_streams"),
    SECURITY_CRYPTO("security_crypto"),
    TESTING_QUALITY("testing_quality"),
    BUILD_DEPENDENCY("build_dependency"),
    CICD_DEVOPS("cicd_devops"),
    TOOLS_EDITORS("tools_editors"),
    DEBUG_PROFILE("debug_profile"),
    ERRORS_LOGGING("errors_logging"),
    CONCURRENCY_MEMORY("concurrency_memory"),
    ARCHITECTURE_PATTERNS("architecture_patterns"),
    DOCS_REQUIREMENTS("docs_requirements"),
    PROCESS_METHODOLOGY("process_methodology"),
    I18N_REGEX("i18n_regex"),
    MOBILE("mobile"),
    GRAPHICS_GAME("graphics_game"),
    DATA_ML_AI("data_ml_ai"),
    LICENSING("licensing"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryProgramming? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryProgramming =
            fromKey(key) ?: OTHER
    }
}
