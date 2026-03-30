package com.sleeplessdog.pimi.games.domain.usecases

import com.sleeplessdog.pimi.games.data.repository.WordsRepository
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.games.presentation.models.Word

class AddWordToUserGroupUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(
        groupId: String,
        origin: String,
        translate: String,
        study: Language,
        ui: Language,
    ) {
        repository.addWordUserDB(
            groupId = groupId,
            origin = origin,
            translate = translate,
            originLanguage = study,
            translateLanguage = ui
        )
    }
}

/**
 * endGame функция для добавления слов из основного словаря в словарь пользователя
 */
class AddWordToUserDictionaryUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(globalIds: List<Int>) {
        repository.addGlobalWordsListToUserWords(globalIds)
    }
}

class AddSingleWordToSavedWordsUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(word: WordUi) {
        repository.addSingleWordToSavedWordsUC(word)
    }
}

class EditWordInUserGroupUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(
        groupId: String,
        wordId: Long,
        origin: String,
        translate: String,
        study: Language,
        ui: Language,
    ) {
        repository.editWordUserDB(
            groupId,
            wordId,
            origin,
            translate,
            study,
            ui,
        )
    }
}

class DeleteWordFromUserGroupUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(groupId: String, wordId: Long) {
        repository.deleteWord(groupId, wordId)
    }
}

class MoveWordToUserGroupUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(
        wordId: Long,
        targetGroupId: String,
    ) {
        repository.moveWord(wordId, targetGroupId)
    }
}

class GetWordPairsFromUserGroupUC(
    private val repository: WordsRepository,
) {
    suspend operator fun invoke(
        lang1: Language,
        lang2: Language,
        groupKey: String,
        wordsNeeded: Int,
    ): List<Pair<Word, Word>> {
        return repository.getWordPairsFromUserGroup(lang1, lang2, groupKey, wordsNeeded)
    }
}