package com.sleeplessdog.matchthewords.game.presentation.holders

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.ItemLetterBinding
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordLetterUi
import com.sleeplessdog.matchthewords.utils.ConstantsApp.FULL_ALPHA
import com.sleeplessdog.matchthewords.utils.ConstantsApp.HALF_ALPHA_WTW
import com.sleeplessdog.matchthewords.utils.ConstantsTimeReaction.PRESS_DELAY_MS

class LettersAdapter(
    private val onClick: (position: Int) -> Unit
) : ListAdapter<WriteTheWordLetterUi, LettersAdapter.VH>(DIFF) {

    var locked: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_LOCKED_CHANGED)
        }

    private val uiHandler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLetterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding) { pos -> handleClickOptimistic(pos) }
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.contains(PAYLOAD_LOCKED_CHANGED)) {
            holder.updateLockState(getItem(position), locked)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), locked)
    }

    private fun handleClickOptimistic(position: Int) {
        val item = currentList.getOrNull(position)

        if (item == null || locked || item.used ||  position == RecyclerView.NO_POSITION) return

        val newItem = item.copy(used = true)
        val newList = currentList.toMutableList().also { it[position] = newItem }
        submitList(newList)
        uiHandler.postDelayed({ onClick(position) }, PRESS_DELAY_MS)
    }

    class VH(
        private val binding: ItemLetterBinding,
        private val onOptimisticClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onOptimisticClick(bindingAdapterPosition)
            }
        }

        fun bind(item: WriteTheWordLetterUi, locked: Boolean) {
            val isSpace = item.char == ' '

            binding.iconSpace.visibility = if (isSpace) View.VISIBLE else View.GONE
            binding.tLetter.isInvisible = isSpace
            if (!isSpace) {
                binding.tLetter.text = item.char.toString()
            } else {
                binding.tLetter.text = ""
                binding.iconSpace.setImageResource(R.drawable.ic_category_abstract)
            }
            updateLockState(item, locked)
        }

        fun updateLockState(item: WriteTheWordLetterUi, locked: Boolean) {
            val enabled = !item.used && !locked
            binding.root.isEnabled = enabled
            binding.root.alpha = if (enabled) FULL_ALPHA else HALF_ALPHA_WTW
        }
    }

    companion object {
        private const val PAYLOAD_LOCKED_CHANGED = "PAYLOAD_LOCKED_CHANGED"
        private val DIFF = object : DiffUtil.ItemCallback<WriteTheWordLetterUi>() {
            override fun areItemsTheSame(oldItem: WriteTheWordLetterUi, newItem: WriteTheWordLetterUi) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WriteTheWordLetterUi, newItem: WriteTheWordLetterUi) =
                oldItem == newItem
        }
    }
}
