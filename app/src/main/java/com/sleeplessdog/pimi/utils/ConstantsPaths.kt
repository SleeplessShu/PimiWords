package com.sleeplessdog.pimi.utils

object ConstantsPaths {
    const val LANDING_SCREENS_PREFS = "welcome_screens_prefs"

    const val FIREBASE_KEY =
        "https://match-the-words-d26c2-default-rtdb.europe-west1.firebasedatabase.app"
    const val FIREBASE_STORAGE_BUCKET = "gs://your-project-id.appspot.com"

    const val NETWORK_DATABASE_PATH_ON_FIREBASE = "actual_db/global_dictionary.db"
    const val GLOBAL_DATABASE_DICTIONARY_NAME = "global_dictionary.db"
    const val USER_DATABASE_DICTIONARY_NAME = "user_dictionary.db"
    const val ASSETS_DATABASE_DICTIONARY_PATH = "databases/global_dictionary.db"

    //---SharedPreferences access---
    const val SHARED_PREFS_SCORE_REPOSITORY = "ScoreHistory"
    const val SHARED_PREFS_SCORE_KEY = "scoreStore"

    const val SHARED_PREFS_THEME_REPOSITORY = "NightMode"
    const val SHARED_PREFS_THEME_KEY = "themePreferences"

    const val SHARED_PREFS_DATABASE_SETTINGS = "db_prefs"
    const val GLOBAL_DATABASE_DICTIONARY_DATE = "global_db_date"
    const val USER_DATABASE_DICTIONARY_DATE = "user_db_date"

    //---DEBUGGING TAG NAMES---
    const val TAG_MAIN_ACTIVITY = "MainActivity_Debugging"

    const val TOTAL_POINTS = "total_points"
    const val CURRENT_STREAK = "current_streak"
    const val LAST_PLAY_DATE = "last_play_date" // yyyy-MM-dd
    const val TOTAL_GAMES = "total_games"

    const val SAVED_GROUP_KEY = "saved_words"
}
