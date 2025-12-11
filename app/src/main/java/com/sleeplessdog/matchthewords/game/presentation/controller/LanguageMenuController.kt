package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.LanguageAdapterState
import com.sleeplessdog.matchthewords.utils.ConstantsApp

class LanguageMenuController(
    private val root: View,              // binding.languageSelectRoot
    private val bg: View,                // binding.languagesBackground
    private val bgSolid: View,           // binding.languagesBackgroundSolid
    private val titleTv: TextView        // binding.tvLanguageList
) {

    init {
        // Сразу настраиваем закрытие по клику на фон
        val closeListener = View.OnClickListener { hide() }
        bg.setOnClickListener(closeListener)
        bgSolid.setOnClickListener(closeListener)
    }

    /**
     * Универсальный метод показа
     * @param titleResId - ID строки заголовка (R.string.std_language и т.д.)
     * @param onUpdateData - Лямбда, в которой Фрагмент обновит данные адаптера
     */
    fun show(titleResId: Int, onUpdateData: () -> Unit) {
        // Если уже открыто и полностью видно - закрываем (режим toggle)
        if (root.isVisible && root.alpha == ConstantsApp.FULL_ALPHA) {
            hide()
            return
        }

        // 1. Устанавливаем заголовок
        titleTv.setText(titleResId)

        // 2. Выполняем обновление данных (вызывается код из фрагмента)
        onUpdateData()

        // 3. Анимация появления меню
        root.visibility = View.VISIBLE
        root.alpha = ConstantsApp.EMPTY_ALPHA
        root.scaleY = ConstantsApp.ZERO_SCALE
        root.pivotY = ConstantsApp.ZERO_SCALE
        root.animate().alpha(ConstantsApp.FULL_ALPHA).scaleY(ConstantsApp.FULL_SCALE)
            .setDuration(ConstantsApp.FADE_IN_DURATION_MS).start()

        // 4. Анимация фона
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
        uiList: List<Language>?,
        studyList: List<Language>?,
        uiSelected: Language?,
        studySelected: Language?,
        adapter: LanguageAdapter
    ) {
        val isUi = mode == LanguageAdapterState.UI
        val titleRes = if (isUi) R.string.int_language else R.string.std_language

        val list = if (isUi) uiList else studyList
        val selected = if (isUi) uiSelected else studySelected

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
