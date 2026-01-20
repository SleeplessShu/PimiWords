package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PimiRecyclerViewAdapter(private val recyclerView: RecyclerView) : PimiScrollable {
    override val view: View
        get() = recyclerView

    override val currentScrollY: Int
        get() = recyclerView.computeVerticalScrollOffset()

    override fun getScrollRange(): Int {
        return recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()
    }

    override fun scrollTo(y: Int) {
        val current = currentScrollY
        val dy = y - current
        recyclerView.scrollBy(0, dy)
    }

    override fun addOnScrollListener(listener: () -> Unit) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                listener()
            }
        })
    }
}