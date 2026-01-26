package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View
import android.widget.ScrollView
import com.sleeplessdog.matchthewords.game.presentation.view.PimiScrollable
import kotlin.math.max

class PimiScrollViewAdapter(private val scrollView: ScrollView) : PimiScrollable {
    override val view: View
        get() = scrollView

    override val currentScrollY: Int
        get() = scrollView.scrollY

    override fun getScrollRange(): Int {
        val content = scrollView.getChildAt(0) ?: return 0
        return max(0, content.height - scrollView.height)
    }

    override fun scrollTo(y: Int) {
        scrollView.scrollTo(0, y)
    }

    override fun addOnScrollListener(listener: () -> Unit) {
        scrollView.viewTreeObserver.addOnScrollChangedListener(listener)
    }
}