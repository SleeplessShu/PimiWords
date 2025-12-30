package com.sleeplessdog.matchthewords.game.presentation.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.databinding.EndGameRwCheckboxElementBinding
import com.sleeplessdog.matchthewords.game.presentation.models.Word

data class SelectableWordPair(
    val pair: Pair<Word, Word>,
    var isSelected: Boolean = false,
)

class EndGameWordsAdapter(
    private val onSelectionChanged: (selectedPairs: List<Pair<Word, Word>>) -> Unit,
) : RecyclerView.Adapter<EndGameWordsAdapter.WordPairViewHolder>() {

    // Внутренний список для хранения пар и их состояния "выбрано"
    private var items: List<SelectableWordPair> = emptyList()

    // Внутренний ViewHolder, который знает о своем биндинге
    inner class WordPairViewHolder(val binding: EndGameRwCheckboxElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Устанавливаем слушатель клика на весь элемент (itemView)
            itemView.setOnClickListener {
                // Проверяем, что позиция адаптера валидна
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = items[adapterPosition]
                    // Инвертируем состояние выбора
                    item.isSelected = !item.isSelected
                    // Обновляем элемент в списке, чтобы RecyclerView перерисовал его
                    notifyItemChanged(adapterPosition)
                    // Сообщаем наружу об изменении выбора
                    reportSelectionChange()
                }
            }
        }

        fun bind(selectablePair: SelectableWordPair) {
            // Заполняем текстовые поля
            binding.tvOrigin.text = selectablePair.pair.first.text
            binding.tvTranslate.text = selectablePair.pair.second.text
            // Устанавливаем состояние чекбокса
            binding.checkbox.isChecked = selectablePair.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordPairViewHolder {
        // Создаем биндинг для нашего элемента списка
        val binding = EndGameRwCheckboxElementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordPairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordPairViewHolder, position: Int) {
        // Передаем данные в ViewHolder для отрисовки
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // Публичный метод для установки данных в адаптер
    fun submitList(pairs: List<Pair<Word, Word>>) {
        // Преобразуем входящий список пар в наш внутренний формат с состоянием выбора
        this.items = pairs.map { SelectableWordPair(it) }
        // Уведомляем адаптер, что данные полностью изменились
        notifyDataSetChanged()
        // Сразу после установки новых данных сообщаем, что ничего не выбрано
        reportSelectionChange()
    }

    // Вспомогательный метод, чтобы отправить обновленный список выбранных пар
    private fun reportSelectionChange() {
        val selected = items.filter { it.isSelected }.map { it.pair }
        onSelectionChanged(selected)
    }
}