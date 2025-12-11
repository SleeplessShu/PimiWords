package com.sleeplessdog.matchthewords.game.presentation.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.controller.WordStateResolver
import com.sleeplessdog.matchthewords.game.presentation.controller.WordViewStateApplier
import com.sleeplessdog.matchthewords.game.presentation.models.Word

class WordsMatchingAdapter(
    private val onWordClick: (Word) -> Unit,
) : ListAdapter<Pair<Word, Word>, ViewHolderWordsMatching>(WordsDiffCallback) {

    private val stateResolver = WordStateResolver()

    var selectedWords: List<Word> = emptyList()
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    var errorWords: List<Word> = emptyList()
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    var usedWords: Set<Int> = emptySet()
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    var correctWords: Set<Int> = emptySet()
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWordsMatching {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.words_pair, parent, false)
        return ViewHolderWordsMatching(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolderWordsMatching,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_STATE_CHANGED)) {
            val (origin, translate) = getItem(position)
            applyState(holder, origin, isLeft = true)
            applyState(holder, translate, isLeft = false)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderWordsMatching, position: Int) {
        val (origin, translate) = getItem(position)

        holder.origin.text = origin.text
        holder.translate.text = translate.text

        // Устанавливаем слушатели один раз
        holder.origin.setOnClickListener { onWordClick(origin) }
        holder.translate.setOnClickListener { onWordClick(translate) }

        // Применяем актуальное состояние
        applyState(holder, origin, isLeft = true)
        applyState(holder, translate, isLeft = false)
    }

    private fun applyState(holder: ViewHolderWordsMatching, word: Word, isLeft: Boolean) {
        val btn = if (isLeft) holder.origin else holder.translate
        val pimi = if (isLeft) holder.originPimi else holder.translatePimi

        // Вычисляем состояние на основе свойств адаптера
        val state = stateResolver.resolve(
            word, selectedWords, errorWords, usedWords, correctWords
        )

        WordViewStateApplier.apply(
            button = btn,
            pimi = pimi,
            state = state,
            isUsed = word.id in usedWords,
            ctx = holder.itemView.context
        )
    }

    companion object {
        private const val PAYLOAD_STATE_CHANGED = "PAYLOAD_STATE_CHANGED"

        private val WordsDiffCallback = object : DiffUtil.ItemCallback<Pair<Word, Word>>() {
            override fun areItemsTheSame(
                oldItem: Pair<Word, Word>,
                newItem: Pair<Word, Word>
            ): Boolean {
                // Сравниваем ID слов, чтобы понять, та же ли это пара
                return oldItem.first.id == newItem.first.id &&
                        oldItem.second.id == newItem.second.id
            }

            override fun areContentsTheSame(
                oldItem: Pair<Word, Word>,
                newItem: Pair<Word, Word>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
