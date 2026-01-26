package com.sleeplessdog.matchthewords.backend.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppStatistics")
data class AppStatisticsEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,

    val value: String,
)
