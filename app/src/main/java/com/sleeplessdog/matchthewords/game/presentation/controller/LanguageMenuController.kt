package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.LanguageAdapterState
import com.sleeplessdog.matchthewords.utils.ConstantsApp

data class MenuData(
    val uiList: List<Language>?,
    val studyList: List<Language>?,
    val uiSelected: Language?,
    val studySelected: Language?
)

class LanguageMenuController(
    private val root: View,              // binding.languageSelectRoot
    private val bg: View,                // binding.languagesBackground
    private val bgSolid: View,           // binding.languagesBackgroundSolid
    private val titleTv: TextView        // binding.tvLanguageList
) {

    init {
        val closeListener = View.OnClickListener { hide() }
        bg.setOnClickListener(closeListener)
        bgSolid.setOnClickListener(closeListener)
    }

    fun show(titleResId: Int, onUpdateData: () -> Unit) {
        if (root.isVisible && root.alpha == ConstantsApp.FULL_ALPHA) {
            hide()
            return
        }

        titleTv.setText(titleResId)

        onUpdateData()

        root.visibility = View.VISIBLE
        root.alpha = ConstantsApp.EMPTY_ALPHA
        root.scaleY = ConstantsApp.ZERO_SCALE
        root.pivotY = ConstantsApp.ZERO_SCALE
        root.animate().alpha(ConstantsApp.FULL_ALPHA).scaleY(ConstantsApp.FULL_SCALE)
            .setDuration(ConstantsApp.FADE_IN_DURATION_MS).start()

        showBgIfNeeded()
    }

    fun hide() {
        if (root.isVisible) {
            root.animate().alpha(ConstantsApp.EMPTY_ALPHA).scaleY(ConstantsApp.ZERO_SCALE)
                .setDuration(ConstantsApp.FADE_IN_DURATION_MS)
                .withEndAction { root.visibility = View.GONE }.start()
        }
        hideBg()
    }

    fun openMenuForMode(
        mode: LanguageAdapterState,
        data: MenuData,
        adapter: LanguageAdapter
    ) {
        val isUi = mode == LanguageAdapterState.UI
        val titleRes = if (isUi) R.string.int_language else R.string.std_language

        val list = if (isUi) data.uiList else data.studyList
        val selected = if (isUi) data.uiSelected else data.studySelected

        this.show(titleRes) {
            adapter.submit(list ?: emptyList(), selected)
            selected?.let { adapter.setSelected(it) }
        }
    }

    private fun showBgIfNeeded() {
        if (!bg.isVisible) {
            bg.visibility = View.VISIBLE
            bg.alpha = ConstantsApp.EMPTY_ALPHA
            bg.animate().alpha(ConstantsApp.FULL_ALPHA)
                .setDuration(ConstantsApp.FADE_IN_DURATION_MS).start()
        }
        if (!bgSolid.isVisible) {
            bgSolid.visibility = View.VISIBLE
            bgSolid.alpha = ConstantsApp.EMPTY_ALPHA
            bgSolid.animate().alpha(ConstantsApp.FULL_ALPHA)
                .setDuration(ConstantsApp.FADE_IN_DURATION_MS).start()
        }
    }

    private fun hideBg() {
        if (bg.isVisible) {
            bg.animate().alpha(ConstantsApp.EMPTY_ALPHA)
                .setDuration(ConstantsApp.FADE_IN_DURATION_MS)
                .withEndAction { bg.visibility = View.GONE }.start()
        }
        if (bgSolid.isVisible) {
            bgSolid.animate().alpha(ConstantsApp.EMPTY_ALPHA)
                .setDuration(ConstantsApp.FADE_IN_DURATION_MS)
                .withEndAction { bgSolid.visibility = View.GONE }.start()
        }
    }
}
