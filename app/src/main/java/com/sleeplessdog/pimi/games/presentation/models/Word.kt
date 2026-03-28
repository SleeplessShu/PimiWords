package com.sleeplessdog.pimi.games.presentation.models

import com.sleeplessdog.pimi.settings.Language


data class Word(
    val id: Int,
    val text: String,
    val language: Language,
    val isValid: Boolean = true,
) {
    companion object {
        fun invalid(language: Language): Word =
            Word(id = -1, text = "—", language = language, isValid = false)
    }
}