package com.sleeplessdog.matchthewords.backend.presentation.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_groups",
    indices = [Index(value = ["key"], unique = true)]
)
data class WordGroupPresentation(
    @PrimaryKey val id: Int? = null,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "title_key") val titleKey: String,
    @ColumnInfo(name = "icon_key") val iconKey: String,
    @ColumnInfo(name = "is_selected") val isSelected: Boolean = false,
    @ColumnInfo(name = "is_user") val isUser: Boolean = false,
    @ColumnInfo(name = "order_in_block") val orderInBlock: Int = 0,
)