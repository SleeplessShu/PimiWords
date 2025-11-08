package com.sleeplessdog.matchthewords.settings.data

import android.content.Context
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.settings.domain.models.EmailData
import com.sleeplessdog.matchthewords.settings.domain.repositories.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {


    override fun getShareAppLink(): String {
        return context.getString(R.string.shareApp)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.supportEmail),
            context.getString(R.string.empty),
            context.getString(R.string.empty)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.linkToAgreement)
    }
}