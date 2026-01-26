package com.sleeplessdog.matchthewords.game.presentation.view

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

class LanguageMenuManager(
    private val root: View,              // binding.languageSelectRoot
    private val bg: View,                // binding.languagesBackground
    private val bgSolid: View,           // binding.languagesBackgroundSolid
    private val titleTv: TextView,        // binding.tvLanguageList
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
        if (root.isVisible && root.alpha == 1f) {
            hide()
            return
        }

        // 1. Устанавливаем заголовок
        titleTv.setText(titleResId)

        // 2. Выполняем обновление данных (вызывается код из фрагмента)
        onUpdateData()

        // 3. Анимация появления меню
        root.visibility = View.VISIBLE
        root.alpha = 0f
        root.scaleY = 0f
        root.pivotY = 0f
        root.animate()
            .alpha(1f)
            .scaleY(1f)
            .setDuration(200)
            .start()

        // 4. Анимация фона
        showBgIfNeeded()
    }

    fun hide() {
        if (root.isVisible) {
            root.animate()
                .alpha(0f)
                .scaleY(0f)
                .setDuration(200)
                .withEndAction { root.visibility = View.GONE }
                .start()
        }
        hideBg()
    }

    private fun showBgIfNeeded() {
        if (!bg.isVisible) {
            bg.visibility = View.VISIBLE
            bg.alpha = 0f
            bg.animate().alpha(1f).setDuration(200).start()
        }
        if (!bgSolid.isVisible) {
            bgSolid.visibility = View.VISIBLE
            bgSolid.alpha = 0f
            bgSolid.animate().alpha(1f).setDuration(200).start()
        }
    }

    private fun hideBg() {
        if (bg.isVisible) {
            bg.animate().alpha(0f).setDuration(200)
                .withEndAction { bg.visibility = View.GONE }.start()
        }
        if (bgSolid.isVisible) {
            bgSolid.animate().alpha(0f).setDuration(200)
                .withEndAction { bgSolid.visibility = View.GONE }.start()
        }
    }
}