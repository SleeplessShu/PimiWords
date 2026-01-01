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


    private var items: List<SelectableWordPair> = emptyList()

    inner class WordPairViewHolder(val binding: EndGameRwCheckboxElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = items[adapterPosition]
                    item.isSelected = !item.isSelected
                    notifyItemChanged(adapterPosition)
                    reportSelectionChange()
                }
            }
        }

        fun bind(selectablePair: SelectableWordPair) {
            binding.tvOrigin.text = selectablePair.pair.first.text
            binding.tvTranslate.text = selectablePair.pair.second.text
            binding.checkbox.isChecked = selectablePair.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordPairViewHolder {
        val binding = EndGameRwCheckboxElementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordPairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordPairViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(pairs: List<Pair<Word, Word>>) {
        this.items = pairs.map { SelectableWordPair(it) }
        notifyDataSetChanged()
        reportSelectionChange()
    }

    fun toggleSelectAll(isSelected: Boolean) {
        items.forEach { it.isSelected = isSelected }
        notifyDataSetChanged()
        reportSelectionChange()
    }

    private fun reportSelectionChange() {
        val selected = items.filter { it.isSelected }.map { it.pair }
        onSelectionChanged(selected)
    }
}