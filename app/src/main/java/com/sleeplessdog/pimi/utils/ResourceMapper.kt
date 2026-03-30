package com.sleeplessdog.pimi.utils

import android.content.Context
import com.sleeplessdog.matchthewords.R


fun Context.groupTitleRes(key: String): Int {
    if (key.equals("saved_words")) return R.string.group_saved
    val normalized = normalizeGroupKey(key)
    return resources.getIdentifier("group_$normalized", "string", packageName).takeIf { it != 0 }
        ?: R.string.group_unknown
}

fun Context.groupIconRes(key: String): Int {
    val normalized = normalizeGroupKey(key)

    return resources.getIdentifier("ic_group_$normalized", "drawable", packageName)
        .takeIf { it != 0 } ?: R.drawable.ic_group_default
}

private fun normalizeGroupKey(key: String): String {
    return GROUP_KEY_ALIAS[key] ?: key
}

private val GROUP_KEY_ALIAS = mapOf(
    "general_adjectives" to "adjectives",
    "general_adverbs" to "adverbs",
    "general_pronouns" to "pronouns"
)