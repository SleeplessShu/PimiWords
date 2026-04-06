package com.sleeplessdog.pimi.server.domain

import java.io.File

interface ServerDbInteractor {
    suspend fun checkForUpdate(localDate: String): Result<String?>
    suspend fun downloadDatabase(localFile: File): Result<Unit>
}