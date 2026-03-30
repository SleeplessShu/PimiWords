package com.sleeplessdog.pimi.payments

import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import kotlinx.coroutines.flow.StateFlow

class PremiumGate(
    private val billingManager: BillingManager,
    private val appPrefs: AppPrefs,
) {
    val isPremium: StateFlow<Boolean> = billingManager.isPremium

    fun check(): Boolean = true//appPrefs.isPremium() || billingManager.isPremium.value
}