package com.sleeplessdog.pimi.di


import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.global.GlobalDatabase
import com.sleeplessdog.pimi.database.user.UserDatabase
import com.sleeplessdog.pimi.dictionary.word_packs.GetWordPacksUC
import com.sleeplessdog.pimi.dictionary.word_packs.InstallWordPackUC
import com.sleeplessdog.pimi.endGame.ReportWordMistakeUC
import com.sleeplessdog.pimi.games.data.repository.GroupsRepository
import com.sleeplessdog.pimi.games.data.repository.SettingsRepository
import com.sleeplessdog.pimi.games.data.repository.WordsRepository
import com.sleeplessdog.pimi.games.domain.models.WordsController
import com.sleeplessdog.pimi.games.domain.usecases.AddSingleWordToSavedWordsUC
import com.sleeplessdog.pimi.games.domain.usecases.AddWordToUserDictionaryUC
import com.sleeplessdog.pimi.games.domain.usecases.AddWordToUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.DeleteUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.DeleteWordFromUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.EditWordInUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.GetGlobalGroupWordsOnceUC
import com.sleeplessdog.pimi.games.domain.usecases.GetGlobalGroupsOnceUC
import com.sleeplessdog.pimi.games.domain.usecases.GetSelectedGroupsUC
import com.sleeplessdog.pimi.games.domain.usecases.GetWordPairsFromUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.GetWordsCountForGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.GetWordsCountUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.MoveWordToUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.ObserveAllGroupsForDictionaryUC
import com.sleeplessdog.pimi.games.domain.usecases.ObserveUserGroupsForGroupsUC
import com.sleeplessdog.pimi.games.domain.usecases.ObserveUserGroupsUC
import com.sleeplessdog.pimi.games.domain.usecases.ObserveWordsInUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.RenameUserGroupUC
import com.sleeplessdog.pimi.score.GetScoreUiStateUC
import com.sleeplessdog.pimi.score.ProcessGameResultUC
import com.sleeplessdog.pimi.score.StatsRepository
import com.sleeplessdog.pimi.settings.ObserveAllGroupsForSettingsUC
import com.sleeplessdog.pimi.settings.SettingsObserveLevelsUC
import com.sleeplessdog.pimi.settings.SettingsSaveLevelsUC
import com.sleeplessdog.pimi.settings.SettingsSaveSelectionUC
import com.sleeplessdog.pimi.settings.SettingsToggleCategoryUC
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    // -------- Global DB --------
    single {
        GlobalDatabase.create(androidContext())
    }

    single {
        get<GlobalDatabase>().globalDao()
    }

    // -------- User DB --------
    single {
        UserDatabase.create(androidContext())
    }

    single {
        get<UserDatabase>().userDao()
    }

    single {
        get<UserDatabase>().wordProgressDao()
    }

    // -------- Repository --------
    single {
        WordsRepository(
            get()
        )
    }

    single {
        StatsRepository(
            databaseProvider = get()
        )
    }

    // -------- Controller --------
    single {
        WordsController(
            repository = get()
        )
    }



    single { AppDatabaseProvider(get()) }

    /**
     * settings uc
     */
    single { SettingsRepository(get()) }
    single { SettingsToggleCategoryUC(get()) }
    single { SettingsSaveSelectionUC(get()) }
    single { SettingsSaveLevelsUC(get()) }
    single { SettingsObserveLevelsUC(get()) }
    single { ObserveAllGroupsForSettingsUC(get()) }
    /**
     * group uc
     */
    single { GroupsRepository(get()) }
    single { CreateUserGroupUC(get()) }
    single { GetSelectedGroupsUC(get()) }
    single { GetWordsCountForGroupUC(get()) }
    single { GetGlobalGroupsOnceUC(get()) }
    single { GetWordsCountUserGroupUC(get()) }
    single { ObserveUserGroupsUC(get()) }
    single { ObserveAllGroupsForDictionaryUC(get()) }
    single { GetGlobalGroupWordsOnceUC(get()) }
    single { RenameUserGroupUC(get()) }
    single { DeleteUserGroupUC(get()) }
    /**
     * word uc
     */
    single { AddWordToUserDictionaryUC(get()) }
    single { EditWordInUserGroupUC(get()) }
    single { DeleteWordFromUserGroupUC(get()) }
    single { MoveWordToUserGroupUC(get()) }
    single { ObserveWordsInUserGroupUC(get()) }
    single { ObserveUserGroupsForGroupsUC(get()) }
    single { AddWordToUserGroupUC(get()) }
    single { AddSingleWordToSavedWordsUC(get()) }
    single { GetWordPairsFromUserGroupUC(get()) }
    single { ReportWordMistakeUC(get(), get(), get()) }
    /**
     * score uc
     */
    single { ProcessGameResultUC(get()) }
    single { GetScoreUiStateUC(get(), get(), get()) }
    /**
     * word packs
     */
    single { GetWordPacksUC(get()) }
    single {
        InstallWordPackUC(
            groupsRepository = get(),
            wordsRepository = get(),
            storage = get(),
            appPrefs = get(),
        )
    }
}
