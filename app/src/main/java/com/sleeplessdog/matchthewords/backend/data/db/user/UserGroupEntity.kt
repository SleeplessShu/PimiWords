package com.sleeplessdog.matchthewords.backend.data.db.user


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserGroups",
    indices = [
        Index(value = ["groupKey"], unique = true)
    ]
)
data class UserGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val groupKey: String, // "saved_words" или UUID

    val title: String, // "Saved words" или пользовательский текст

    val iconKey: String, // например "ic_saved", "ic_folder"
)
