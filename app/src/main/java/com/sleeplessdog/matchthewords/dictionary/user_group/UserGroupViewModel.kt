package com.sleeplessdog.matchthewords.dictionary.user_group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.domain.usecases.words.GetWordsByGroupUC
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserGroupViewModel(
    private val getWordsByGroup: GetWordsByGroupUC,
    private val repository: AppPrefs
) : ViewModel() {

    private val _wordsState = MutableStateFlow<List<WordWithTranslation>>(emptyList())
    val wordsState: StateFlow<List<WordWithTranslation>> get() = _wordsState

   fun loadWords(groupKey: String) {
        viewModelScope.launch {
            val it = getWordsByGroup.getWordsByGroup(groupKey)
            val languages = getLanguages()
            val wordWithTranslationList: List<WordWithTranslation> = it.map {

                val uiLanguage = when (languages[0]) {
                    Language.ENGLISH -> it.english
                    Language.SPANISH -> it.spanish
                    Language.RUSSIAN -> it.russian
                    Language.FRENCH -> it.french
                    Language.GERMAN -> it.german
                    Language.ARMENIAN -> it.armenian
                    Language.SERBIAN -> it.serbian
                }
                val studyLanguage = when (languages[1]) {
                    Language.ENGLISH -> it.english
                    Language.SPANISH -> it.spanish
                    Language.RUSSIAN -> it.russian
                    Language.FRENCH -> it.french
                    Language.GERMAN -> it.german
                    Language.ARMENIAN -> it.armenian
                    Language.SERBIAN -> it.serbian
                }
                WordWithTranslation(word = uiLanguage, translation = studyLanguage)
            }
            _wordsState.value = wordWithTranslationList
        }
    }

    private fun getLanguages(): List<Language> {
        val uiLanguage = repository.getUiLanguage()
        val studyLanguage = repository.getStudyLanguage()
        return listOf(uiLanguage, studyLanguage)
    }
}