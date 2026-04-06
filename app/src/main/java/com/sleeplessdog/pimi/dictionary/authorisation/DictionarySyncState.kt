package com.sleeplessdog.pimi.dictionary.authorisation

import com.sleeplessdog.pimi.R

data class DictionarySyncState(
    val auth: AuthState = AuthState.UNKNOWN,
    val globalDb: DataTransferStatus = DataTransferStatus.UNKNOWN,
    val userDb: DataTransferStatus = DataTransferStatus.UNKNOWN,
)

enum class AuthState {
    UNKNOWN, NOT_AUTHORIZED, AUTHORIZING, AUTHORIZED, ERROR
}

fun AuthState.toImageRes(): Int = when (this) {
    AuthState.UNKNOWN -> R.drawable.ic_circle_default
    AuthState.NOT_AUTHORIZED -> R.drawable.ic_user_unlogged
    AuthState.AUTHORIZING -> R.drawable.ic_logging
    AuthState.AUTHORIZED -> R.drawable.ic_user_llogged
    AuthState.ERROR -> R.drawable.ic_error
}

enum class DataTransferStatus {
    DEFAULT, DOWNLOADING, NOT_CONNECTED, UPLOADING, SUCCESS, REFRESHING, ERROR, UNKNOWN, ASSETS, NETWORK, DEPLOYING
}

enum class DatabaseInstance {
    USER, GLOBAL
}

fun DataTransferStatus.toImageRes(): Int = when (this) {
    DataTransferStatus.DEFAULT -> R.drawable.ic_cloud_default
    DataTransferStatus.DOWNLOADING -> R.drawable.ic_cloud_download
    DataTransferStatus.NOT_CONNECTED -> R.drawable.ic_cloud_not_connected
    DataTransferStatus.UPLOADING -> R.drawable.ic_cloud_upload
    DataTransferStatus.SUCCESS -> R.drawable.ic_cloud_success
    DataTransferStatus.REFRESHING -> R.drawable.ic_cloud_refreshing
    DataTransferStatus.ERROR -> R.drawable.ic_error
    DataTransferStatus.UNKNOWN -> R.drawable.ic_circle_default
    DataTransferStatus.ASSETS -> R.drawable.ic_database_local
    DataTransferStatus.NETWORK -> R.drawable.ic_database_cloud
    DataTransferStatus.DEPLOYING -> R.drawable.ic_data_deploying
}

data class DatabaseSyncState(
    val globalDb: DataTransferStatus = DataTransferStatus.UNKNOWN,
    val userDb: DataTransferStatus = DataTransferStatus.UNKNOWN,
)

fun DictionarySyncState.toSummaryImageRes(): Int {
    if (auth == AuthState.ERROR || globalDb == DataTransferStatus.ERROR || userDb == DataTransferStatus.ERROR) return R.drawable.ic_error
    if (auth == AuthState.NOT_AUTHORIZED) return R.drawable.ic_user_unlogged
    if (auth == AuthState.AUTHORIZING) return R.drawable.ic_logging
    if (globalDb == DataTransferStatus.DOWNLOADING || userDb == DataTransferStatus.DOWNLOADING || globalDb == DataTransferStatus.UPLOADING || userDb == DataTransferStatus.UPLOADING) return R.drawable.ic_cloud_download
    if (globalDb == DataTransferStatus.REFRESHING || userDb == DataTransferStatus.REFRESHING || globalDb == DataTransferStatus.DEPLOYING || userDb == DataTransferStatus.DEPLOYING) return R.drawable.ic_cloud_refreshing
    if (globalDb == DataTransferStatus.SUCCESS || globalDb == DataTransferStatus.ASSETS || globalDb == DataTransferStatus.NETWORK) return R.drawable.ic_cloud_success
    return R.drawable.ic_cloud_default
}

