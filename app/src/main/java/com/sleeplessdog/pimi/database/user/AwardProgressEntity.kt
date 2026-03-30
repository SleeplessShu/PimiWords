package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "award_progress")
data class AwardProgressEntity(

    @PrimaryKey
    val awardId: String,

    val progress: Int,
    val target: Int,
)