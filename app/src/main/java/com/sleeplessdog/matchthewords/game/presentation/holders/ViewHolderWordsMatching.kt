package com.sleeplessdog.matchthewords.game.presentation.holders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.R

class ViewHolderWordsMatching(view: View) : RecyclerView.ViewHolder(view) {
    val origin: Button = view.findViewById(R.id.bOrigin)
    val translate: Button = view.findViewById(R.id.bTranslate)

    val originPimi: ImageView = view.findViewById(R.id.origin_pimi)
    val translatePimi: ImageView = view.findViewById(R.id.translation_pimi)
}