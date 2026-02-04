package com.sleeplessdog.matchthewords.di


import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDatabase
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDatabase
import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository
import com.sleeplessdog.matchthewords.backend.data.repository.WordsRepository
import com.sleeplessdog.matchthewords.backend.domain.models.WordsController
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.GetSelectedGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.GetWordsCountForGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.ObserveAllGroupsGroupedUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.ObserveFeaturedGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.SaveSelectionUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.ToggleCategoryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.score.UpdateScoreProgressUseCase
import com.sleeplessdog.matchthewords.backend.domain.usecases.score.UpdateWordProgressUseCase
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsObserveLevelsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsSaveLevelsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.words.AddWordToUserDictionaryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.words.GetWordsByGroupUC
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

    // -------- Repository --------
    single {
        WordsRepository(
            globalDao = get(),
            userDao = get()
        )
    }

    // -------- Controller --------
    single {
        WordsController(
            repository = get()
        )
    }

    single {
        UpdateWordProgressUseCase(
            userDao = get()
        )
    }

    single {
        AddWordToUserDictionaryUC(
            userDao = get()
        )
    }

    single { GroupsRepository(get(), get()) }

    single { ObserveAllGroupsGroupedUC(get()) }
    single { ObserveFeaturedGroupsUC(get()) }
    single { GetSelectedGroupsUC(get()) }
    single { ToggleCategoryUC(get()) }
    single { SaveSelectionUC(get()) }
    single { CreateUserGroupUC(get()) }
    single { SettingsSaveLevelsUC(get()) }
    single { SettingsObserveLevelsUC(get()) }
    single { UpdateScoreProgressUseCase(get()) }
    single { GetWordsCountForGroupUC(get()) }
    single { GetWordsByGroupUC(get()) }
}
