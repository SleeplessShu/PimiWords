package com.sleeplessdog.matchthewords.backend.domain.models

import com.sleeplessdog.matchthewords.backend.presentation.models.WordGroupPresentation


data class WordGroup(
    val key: String,
    val titleKey: String,
    val iconKey: String,
    val isSelected: Boolean,
    val isUser: Boolean,
    val orderInBlock: Int,
)

fun WordGroupPresentation.toDomain() = WordGroup(
    key, titleKey, iconKey, isSelected, isUser, orderInBlock
)
