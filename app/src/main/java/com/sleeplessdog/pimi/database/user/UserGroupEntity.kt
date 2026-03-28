package com.sleeplessdog.pimi.database.user


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sleeplessdog.pimi.games.domain.models.UserGroupDomainEntity

@Entity(
    tableName = "UserGroups", indices = [Index(value = ["groupKey"], unique = true)]
)
data class UserGroupEntity(
    @PrimaryKey val groupKey: String,
    val title: String,
    val icon: String? = null,
)

fun UserGroupEntity.toDomain() = UserGroupDomainEntity(
    groupKey = groupKey,
    title = title,
    icon = icon,
)