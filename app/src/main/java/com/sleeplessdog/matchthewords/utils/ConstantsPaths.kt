package com.sleeplessdog.matchthewords.utils

object ConstantsPaths {
    const val LANDING_SCREENS_PREFS = "welcome_screens_prefs"

    const val FIREBASE_KEY =
        "https://match-the-words-d26c2-default-rtdb.europe-west1.firebasedatabase.app"
    const val NETWORK_DATABASE_PATH_ON_FIREBASE = "actual_db/pimi_dictionary.db"
    const val LOCAL_DATABASE_DICTIONARY_NAME = "dictionary.db"
    const val USER_DATABASE_DICTIONARY_NAME = "user_dictionary_db"
    const val ASSETS_DATABASE_DICTIONARY_PATH = "databases/dictionary_default.db"

    //---SharedPreferences access---
    const val SHARED_PREFS_SCORE_REPOSITORY = "ScoreHistory"
    const val SHARED_PREFS_SCORE_KEY = "scoreStore"

    const val SHARED_PREFS_THEME_REPOSITORY = "NightMode"
    const val SHARED_PREFS_THEME_KEY = "themePreferences"

    const val SHARED_PREFS_DATABASE_SETTINGS = "db_prefs"
    const val LOCAL_DATABASE_DICTIONARY_DATE = "local_db_date"

    //---DEBUGGING TAG NAMES---
    const val TAG_MAIN_ACTIVITY = "MainActivity_Debugging"
}
