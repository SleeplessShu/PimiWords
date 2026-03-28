package com.sleeplessdog.pimi.di

import com.sleeplessdog.pimi.payments.BillingManager
import com.sleeplessdog.pimi.payments.PremiumGate
import com.sleeplessdog.pimi.score.domain.AwardEngine
import org.koin.dsl.module

val domainModule = module {
    single { AwardEngine(get(), get()) }
    single { BillingManager(get(), get()) }
    single { PremiumGate(get(), get()) }
}