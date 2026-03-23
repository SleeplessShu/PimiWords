package com.sleeplessdog.matchthewords.di

import com.sleeplessdog.matchthewords.score.domain.AwardEngine
import org.koin.dsl.module

val domainModule = module {

/*    single<ScoreInteractor> {
        ScoreInteractorImpl(get())
    }*/

    /*    single<ServerDateInteractor> {
            ServerDateInteractorImpl(get())
        }*/
    single { AwardEngine(get(), get()) }
}