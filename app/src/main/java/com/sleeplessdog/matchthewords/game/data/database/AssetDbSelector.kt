package com.sleeplessdog.matchthewords.game.data.database

import android.content.Context

private val DB_RX = Regex("""dictionary_(\d{4}-\d{2}-\d{2})\.db""")

data class AssetDbSelector(
    val assetPath: String,
    val date: String,
)

fun resolveAssetDatabase(ctx: Context): AssetDbSelector {
    val files = ctx.assets.list("databases")?.toList().orEmpty()
    require(files.isNotEmpty()) { "No assets in /assets/databases" }

    val dated = files.mapNotNull { name ->
        DB_RX.matchEntire(name)?.let { name to it.groupValues[1] } // name -> date
    }

    val pickedName = if (dated.isNotEmpty()) {
        dated.maxBy { it.second }.first
    } else {
        check(files.contains("dictionary_default.db")) {
            "dictionary_default.db not found in assets/databases"
        }
        "dictionary_default.db"
    }

    val date = DB_RX.find(pickedName)?.groupValues?.get(1) ?: "1970-01-01"
    return AssetDbSelector(assetPath = "databases/$pickedName", date = date)
}
