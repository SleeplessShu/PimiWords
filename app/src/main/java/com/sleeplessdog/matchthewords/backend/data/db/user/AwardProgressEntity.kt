package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "award_progress")
data class AwardProgressEntity(

    @PrimaryKey
    val awardId: String,

    val progress: Int,       // текущее значение
    val target: Int,          // цель (10, 100, 7 и т.д.)
)