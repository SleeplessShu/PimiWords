package com.sleeplessdog.matchthewords.game.presentation.holders

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import com.sleeplessdog.matchthewords.game.presentation.models.Word

class WordsMatchingAdapter(
    private var wordsPairs: List<Pair<Word, Word>> = emptyList(),
    private var selectedWords: List<Word> = emptyList(),
    private var errorWords: List<Word> = emptyList(),
    private var usedWords: Set<Int> = emptySet(),
    private var correctWords: Set<Int> = emptySet(),
    private val onWordClick: (Word) -> Unit,
) : RecyclerView.Adapter<ViewHolderWordsMatching>() {

    private data class StatePayload(
        val selected: List<Word>?,
        val error: List<Word>,
        val used: Set<Int>?,
        val correct: Set<Int>?,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWordsMatching {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.words_pair, parent, false)
        return ViewHolderWordsMatching(view)
    }

    override fun getItemCount(): Int = wordsPairs.size

    override fun onBindViewHolder(holder: ViewHolderWordsMatching, position: Int) {
        bindFull(holder, position)
    }

    override fun onBindViewHolder(
        holder: ViewHolderWordsMatching,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            bindFull(holder, position)
        } else {
            val (origin, translate) = wordsPairs[position]
            applyState(holder, origin, isLeft = true)
            applyState(holder, translate, isLeft = false)
        }
    }

    private fun bindFull(holder: ViewHolderWordsMatching, position: Int) {
        val (origin, translate) = wordsPairs[position]

        // текст
        holder.origin.text = origin.text
        holder.translate.text = translate.text

        // клики
        holder.origin.setOnClickListener { onWordClick(origin) }
        holder.translate.setOnClickListener { onWordClick(translate) }

        // состояние
        applyState(holder, origin, isLeft = true)
        applyState(holder, translate, isLeft = false)
    }

    private fun applyState(holder: ViewHolderWordsMatching, word: Word, isLeft: Boolean) {
        val btn = if (isLeft) holder.origin else holder.translate
        val pimi = if (isLeft) holder.originPimi else holder.translatePimi
        val state = stateFor(word)
        val ctx = holder.itemView.context

        // фон + enabled + основной цвет
        btn.setBackgroundResource(state.backgroundRes)
        btn.isEnabled = state.enabled
        btn.setTextColor(ContextCompat.getColor(ctx, state.textColorRes))

        // доп. логика для used
        val isUsed = word.id in usedWords
        if (isUsed) {
            // текст “прячем”
            // вариант 1: прозрачный
            // btn.setTextColor(Color.TRANSPARENT)

            // вариант 2: цвет фона кнопки (если фон тёмный):
            btn.setTextColor(Color.TRANSPARENT) // подбери свой

            // показываем pimi
            pimi.visibility = View.VISIBLE
        } else {
            pimi.visibility = View.GONE
        }
    }


    /** Единый маппинг слова → состояние. */
    private fun stateFor(word: Word): ButtonState = when {
        isError(word) -> ButtonState.ERROR
        word.id in usedWords -> ButtonState.DISABLED
        word.id in correctWords -> ButtonState.CORRECT
        isSelected(word) -> ButtonState.SELECTED
        else -> ButtonState.DEFAULT
    }

    private fun isError(word: Word): Boolean =
        errorWords.any { it === word }

    private fun isSelected(word: Word): Boolean =
        selectedWords.any { it === word }

    // --- публичные апдейты ---

    fun updateWordsList(newWordsPairs: List<Pair<Word, Word>>) {
        wordsPairs = newWordsPairs
        notifyDataSetChanged()
    }

    fun updateSelectedWords(newSelected: List<Word>) {
        selectedWords = newSelected
        notifyDataSetChanged()
    }

    fun updateErrorWords(newError: List<Word>) {
        errorWords = newError
        notifyStatesChanged()
    }

    fun updateCorrectWords(newCorrect: List<Word>) {
        correctWords = newCorrect.map { it.id }.toSet()
        notifyStatesChanged()
    }

    fun updateUsedWords(newUsed: List<Word>) {
        usedWords = newUsed.map { it.id }.toSet()
        notifyStatesChanged()
    }

    private fun notifyStatesChanged() {
        val payload = StatePayload(selectedWords, errorWords, usedWords, correctWords)
        for (i in 0 until itemCount) {
            notifyItemChanged(i, payload)
        }
    }
}
