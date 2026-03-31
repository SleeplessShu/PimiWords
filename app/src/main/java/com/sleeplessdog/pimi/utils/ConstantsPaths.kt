package com.sleeplessdog.pimi.utils

object ConstantsPaths {
    const val LANDING_SCREENS_PREFS = "welcome_screens_prefs"

    const val FIREBASE_KEY =
        "https://match-the-words-d26c2-default-rtdb.europe-west1.firebasedatabase.app"
    const val FIREBASE_STORAGE_BUCKET = "gs://your-project-id.appspot.com"

    const val WORD_PACKS_PATH = "word_packs/index.json"

    const val NETWORK_DATABASE_PATH_ON_FIREBASE = "actual_db/global_dictionary.db"
    const val GLOBAL_DATABASE_DICTIONARY_NAME = "global_dictionary.db"
    const val USER_DATABASE_DICTIONARY_NAME = "user_dictionary.db"
    const val ASSETS_DATABASE_DICTIONARY_PATH = "databases/global_dictionary.db"

    //---SharedPreferences access---
    const val PREFS_NAME = "app_prefs"
    const val PREMIUM_STATUS = "is_premium"
    const val KEY_UI_LANG = "ui_lang"
    const val KEY_STUDY_LANG = "study_lang"
    const val KEY_LEVELS = "levels"
    const val KEY_DIFFICULTY = "difficulty"
    const val IS_PREMIUM = "is_premium"

    const val SHARED_PREFS_SCORE_REPOSITORY = "ScoreHistory"
    const val SHARED_PREFS_SCORE_KEY = "scoreStore"

    const val SHARED_PREFS_THEME_REPOSITORY = "NightMode"
    const val SHARED_PREFS_THEME_KEY = "themePreferences"

    const val SHARED_PREFS_DATABASE_SETTINGS = "db_prefs"
    const val GLOBAL_DATABASE_DICTIONARY_DATE = "global_db_date"
    const val USER_DATABASE_DICTIONARY_DATE = "user_db_date"

    //---Intent navigation---
    const val EXTRA_NAVIGATE_TO = "extra_navigate_to"
    const val NAV_SETTINGS = "nav_settings"
    const val NAV_SCORE_MAIN = "score_main"
    const val NAV_AWARDS = "awards"


    //---DEBUGGING TAG NAMES---
    const val TAG_MAIN_ACTIVITY = "MainActivity_Debugging"
    const val TOTAL_POINTS = "total_points"
    const val CURRENT_STREAK = "current_streak"
    const val LAST_PLAY_DATE = "last_play_date" // yyyy-MM-dd
    const val TOTAL_GAMES = "total_games"
    const val SAVED_GROUP_KEY = "saved_words"
}
