package com.sleeplessdog.pimi.endGame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.sleeplessdog.pimi.databinding.EndGameRwCheckboxElementBinding
import com.sleeplessdog.pimi.games.presentation.models.Word

data class SelectableWordPair(
    val pair: Pair<Word, Word>,
    val isSelected: Boolean = false,
)

class EndGameWordsAdapter(
    private val onSelectionChanged: (ids: List<Int>) -> Unit,
) : ListAdapter<SelectableWordPair, EndGameWordsAdapter.VH>(Diff) {

    inner class VH(private val b: EndGameRwCheckboxElementBinding) :
        RecyclerView.ViewHolder(b.root) {

        init {
            b.root.setOnClickListener {
                val pos =
                    bindingAdapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
                toggleItem(pos)
            }
        }

        fun bind(item: SelectableWordPair) = with(b) {
            tvOrigin.text = item.pair.first.text
            tvTranslate.text = item.pair.second.text
            checkbox.isChecked = item.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        EndGameRwCheckboxElementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    fun submitPairs(pairs: List<Pair<Word, Word>>) {
        val updated = pairs.map { SelectableWordPair(it) }
        submitList(updated)
        notifySelection(updated)
    }

    fun toggleSelectAll(select: Boolean) {
        val updated = currentList.map { it.copy(isSelected = select) }
        submitList(updated)
        notifySelection(updated)
    }


    private fun toggleItem(position: Int) {
        val updated = currentList.toMutableList()
        val item = updated[position]
        updated[position] = item.copy(isSelected = !item.isSelected)

        submitList(updated)
        notifySelection(updated)

    }

    private fun notifySelection(list: List<SelectableWordPair>) {
        onSelectionChanged(list.filter { it.isSelected }.flatMap { it.pair.toList() }.map { it.id }
            .distinct())
    }

    private object Diff : DiffUtil.ItemCallback<SelectableWordPair>() {
        override fun areItemsTheSame(a: SelectableWordPair, b: SelectableWordPair) =
            a.pair.first.id == b.pair.first.id && a.pair.second.id == b.pair.second.id

        override fun areContentsTheSame(a: SelectableWordPair, b: SelectableWordPair) = a == b
    }
}