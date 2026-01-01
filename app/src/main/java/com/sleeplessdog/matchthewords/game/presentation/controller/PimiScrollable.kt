package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View

interface PimiScrollable {
    val view: View
    val currentScrollY: Int
    fun getScrollRange(): Int
    fun scrollTo(y: Int)
    fun addOnScrollListener(listener: () -> Unit)
}