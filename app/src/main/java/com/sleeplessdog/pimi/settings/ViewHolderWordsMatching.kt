package com.sleeplessdog.pimi.settings

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.R

class ViewHolderWordsMatching(view: View) : RecyclerView.ViewHolder(view) {
    val origin: Button = view.findViewById(R.id.bOrigin)
    val translate: Button = view.findViewById(R.id.bTranslate)
}