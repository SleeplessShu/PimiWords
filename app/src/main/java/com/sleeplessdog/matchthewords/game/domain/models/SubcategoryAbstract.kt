package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryAbstract(val key: String) {
    CONCEPT_IDEA("concept_idea"),
    QUALITY_TRAIT("quality_trait"),
    STATE_CONDITION("state_condition"),
    PROCESS_CHANGE("process_change"),
    RELATION_CONNECTION("relation_connection"),
    QUANTITY_EXTENT("quantity_extent"),
    KNOWLEDGE_BELIEF("knowledge_belief"),
    ABILITY_MODALITY("ability_modality"),
    CHANCE_PROBABILITY("chance_probability"),
    ETHICS_VALUES("ethics_values"),
    SOCIETY_CULTURE("society_culture"),
    VALUE_ECONOMICS("value_economics"),
    COMMUNICATION_ABSTRACT("communication_abstract"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryAbstract? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryAbstract =
            fromKey(key) ?: OTHER
    }
}
