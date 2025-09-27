package com.sleeplessdog.matchthewords.game.domain.models

enum class SubcategoryEntertainment(val key: String) {
    MOVIE_CINEMA("movie_cinema"),
    TV_STREAMING("tv_streaming"),
    MUSIC_AUDIO("music_audio"),
    PERFORMING_ARTS("performing_arts"),
    GAMES_VIDEO("games_video"),
    GAMES_BOARD_CARD("games_board_card"),
    READING_COMICS("reading_comics"),
    VENUES_AMUSEMENTS("venues_amusements"),
    EVENT_PARTY_FESTIVAL("event_party_festival"),
    GENRE_STYLE("genre_style"),
    PEOPLE_ROLES("people_roles"),
    PRODUCTION_TERMS("production_terms"),
    EQUIPMENT_MEDIA("equipment_media"),
    TICKETING_SCHEDULE("ticketing_schedule"),
    FAN_FANDOM("fan_fandom"),
    OTHER("other");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String?): SubcategoryEntertainment? =
            key?.lowercase()?.let(byKey::get)

        fun parseOrOther(key: String?): SubcategoryEntertainment =
            fromKey(key) ?: OTHER
    }
}
