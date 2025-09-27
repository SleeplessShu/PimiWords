package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryTravel(val key: String) {
    TRANSPORT_MODE("transport_mode"),
    AIRPORT_AIRLINE("airport_airline"),
    RAIL_ROAD("rail_road"),
    BOOKING_TICKETS("booking_tickets"),
    ACCOMMODATION("accommodation"),
    LUGGAGE_PACKING("luggage_packing"),
    DOCUMENTS_BORDER("documents_border"),
    NAVIGATION_DIRECTION("navigation_direction"),
    MONEY_PAYMENT("money_payment"),
    FOOD_DRINK_OUT("food_drink_out"),
    SIGHTSEEING_ACTIVITY("sightseeing_activity"),
    SAFETY_HEALTH("safety_health"),
    ISSUES_SUPPORT("issues_support"),
    COMMUNICATION_TRAVEL("communication_travel"),
    PLANNING_TIME("planning_time"),
    LOCAL_CULTURE_ETIQUETTE("local_culture_etiquette"),
    WEATHER_TRAVEL("weather_travel"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryTravel? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryTravel =
            fromKey(key) ?: OTHER
    }
}
