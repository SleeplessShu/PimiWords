package com.sleeplessdog.matchthewords.backend.domain.models

import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel

data class DomainWord(
    val globalId: Long?,
    val english: String,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val armenian: String?,
    val serbian: String?,
    val groupKey: String,
    val difficulty: LanguageLevel,
    val isUser: Boolean,
)
