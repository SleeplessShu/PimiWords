package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View
import androidx.core.view.isVisible
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.sleeplessdog.matchthewords.game.presentation.models.CategoryUi
import com.sleeplessdog.matchthewords.game.presentation.models.TopicsMenuViews
import com.sleeplessdog.matchthewords.utils.ConstantsApp

class TopicsMenuController(
    private val topicsMenuViews: TopicsMenuViews,
    private val getPreselectedKeys: () -> Set<String>,
    private val onSaveSelection: (Set<String>) -> Unit,
    private val featuredController: FeaturedCategoriesController
) {

    private var preselected: Set<String> = emptySet()
    private var lastUser: List<CategoryUi> = emptyList()
    private var lastDefaults: List<CategoryUi> = emptyList()

    init {
        topicsMenuViews.root.visibility = View.GONE

        topicsMenuViews.btnShowAll.setOnClickListener {
            preselected = getPreselectedKeys()
            show()
            renderGroups()
        }

        topicsMenuViews.btnCancel.setOnClickListener {
            hide()
        }

        topicsMenuViews.background.setOnClickListener {
            hide()
        }

        topicsMenuViews.btnSave.setOnClickListener {
            val selected = readSelectedKeys()
            onSaveSelection(selected)
            hide()
        }
    }

    fun updateGroups(user: List<CategoryUi>, defaults: List<CategoryUi>) {
        lastUser = user
        lastDefaults = defaults
        if (topicsMenuViews.root.isVisible) {
            renderGroups()
        }
    }

    // ---------- render ----------

    private fun renderGroups() {
        renderGroup(topicsMenuViews.groupUser, lastUser)
        renderGroup(topicsMenuViews.groupDefault, lastDefaults)
    }

    private fun renderGroup(group: FlexboxLayout, items: List<CategoryUi>) {
        group.removeAllViews()
        items.forEach { item ->
            group.addView(
                featuredController.createCategoryChip(group, item).apply {
                    isChecked = item.key in preselected || item.isSelected
                }
            )
        }
    }

    private fun readSelectedKeys(): Set<String> {
        fun collect(group: FlexboxLayout): List<String> =
            (0 until group.childCount).mapNotNull { i ->
                val child = group.getChildAt(i)
                val chip = child as? Chip ?: return@mapNotNull null
                val key = chip.tag as? String ?: chip.text.toString()
                if (chip.isChecked) key else null
            }

        return (collect(topicsMenuViews.groupUser) + collect(topicsMenuViews.groupDefault)).toSet()
    }

    private fun show() {
        if (topicsMenuViews.root.isVisible) return

        topicsMenuViews.root.alpha = ConstantsApp.ZERO_SCALE
        topicsMenuViews.root.visibility = View.VISIBLE
        topicsMenuViews.root.animate()
            .alpha(ConstantsApp.FULL_ALPHA)
            .setDuration(ConstantsApp.ANIMATION_DURATION_FOREGROUND)
            .start()

        topicsMenuViews.background.apply {
            alpha = ConstantsApp.ZERO_SCALE
            visibility = View.VISIBLE
            animate()
                .alpha(ConstantsApp.FULL_ALPHA)
                .setDuration(ConstantsApp.ANIMATION_DURATION_BACKGROUND)
                .start()
        }

        val contentViews = listOf(
            topicsMenuViews.header,
            topicsMenuViews.categoriesScroll,
            topicsMenuViews.bottomButtons
        )
        contentViews.forEach { view ->
            view.alpha = ConstantsApp.ZERO_SCALE
            view.translationY = ConstantsApp.TOPICS_MENU_CONTENT_OFFSET_Y
            view.animate()
                .alpha(ConstantsApp.FULL_ALPHA)
                .translationY(ConstantsApp.ZERO_SCALE)
                .setDuration(ConstantsApp.ANIMATION_DURATION_BACKGROUND)
                .start()
        }
    }

    private fun hide() {
        if (!topicsMenuViews.root.isVisible) return

        topicsMenuViews.background.animate()
            .alpha(ConstantsApp.ZERO_SCALE)
            .setDuration(ConstantsApp.ANIMATION_DURATION_BACKGROUND)
            .withEndAction {
                topicsMenuViews.background.visibility = View.GONE
            }
            .start()

        val contentViews = listOf(
            topicsMenuViews.header,
            topicsMenuViews.categoriesScroll,
            topicsMenuViews.bottomButtons
        )
        var finished = 0
        val total = contentViews.size

        contentViews.forEach { view ->
            view.animate()
                .alpha(ConstantsApp.ZERO_SCALE)
                .translationY(ConstantsApp.TOPICS_MENU_CONTENT_OFFSET_Y)
                .setDuration(ConstantsApp.ANIMATION_DURATION_BACKGROUND)
                .withEndAction {
                    finished++
                    if (finished == total) {
                        topicsMenuViews.root.visibility = View.GONE

                        contentViews.forEach { v ->
                            v.alpha = ConstantsApp.FULL_ALPHA
                            v.translationY = ConstantsApp.ZERO_SCALE
                        }
                        topicsMenuViews.background.alpha = ConstantsApp.FULL_ALPHA
                    }
                }
                .start()
        }
    }
}
