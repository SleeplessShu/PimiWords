package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserWords",
    indices = [
        Index("globalId"),
        Index("groupId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val globalId: Long?, // null = user-only слово

    val groupId: Long,

    val english: String?,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val armenian: String?,
    val serbian: String?,

    val addedAt: Long = System.currentTimeMillis(),
)
