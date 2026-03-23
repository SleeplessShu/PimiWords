package com.sleeplessdog.matchthewords.di


import com.sleeplessdog.matchthewords.backend.data.db.AppDatabaseProvider
import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDatabase
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDatabase
import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository
import com.sleeplessdog.matchthewords.backend.data.repository.StatsRepository
import com.sleeplessdog.matchthewords.backend.data.repository.WordsRepository
import com.sleeplessdog.matchthewords.backend.domain.models.WordsController
import com.sleeplessdog.matchthewords.backend.domain.usecases.AddSingleWordToSavedWordsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.AddWordToUserDictionaryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.AddWordToUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.DeleteUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.DeleteWordFromUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.EditWordInUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetGlobalGroupWordsOnceUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetGlobalGroupsOnceUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetScoreUiStateUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetSelectedGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetWordPairsFromUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetWordsCountForGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetWordsCountUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.MoveWordToUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveAllGroupsForDictionaryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveAllGroupsForSettingsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveUserGroupsForGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveUserGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveWordsInUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ProcessGameResultUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.RenameUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ReportWordMistakeUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.SaveSelectionUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ToggleCategoryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsObserveLevelsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsSaveLevelsUC
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


    single { GroupsRepository(get()) }
    single { ToggleCategoryUC(get()) }
    single { SaveSelectionUC(get()) }
    single { SettingsSaveLevelsUC(get()) }
    single { SettingsObserveLevelsUC(get()) }
    /**
     * group uc
     */
    single { CreateUserGroupUC(get()) }
    single { GetSelectedGroupsUC(get()) }
    single { GetWordsCountForGroupUC(get()) }
    single { GetGlobalGroupsOnceUC(get()) }
    single { GetWordsCountUserGroupUC(get()) }
    single { ObserveUserGroupsUC(get()) }
    single { ObserveAllGroupsForSettingsUC(get()) }
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
}
