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

class LettersAdapter(
    private val onClick: (position: Int) -> Unit
) : ListAdapter<WriteTheWordLetterUi, LettersAdapter.VH>(DIFF) {

    var locked: Boolean = false
        set(value) {
            field = value
            // переотрисуем, чтобы мгновенно заблокировать/разблокировать клики
            notifyDataSetChanged()
        }

    private val uiHandler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLetterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding) { pos -> handleClickOptimistic(pos) }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), locked)
    }

    private fun handleClickOptimistic(position: Int) {
        if (position == RecyclerView.NO_POSITION) return
        val item = currentList.getOrNull(position) ?: return
        if (locked || item.used) return

        // 1) Мгновенно помечаем букву использованной и перерисовываем список
        val newItem = item.copy(used = true)
        val newList = currentList.toMutableList().also { it[position] = newItem }
        submitList(newList)

        // 2) Отдаём событие во VM с небольшой задержкой,
        //    чтобы ripple/нажатие визуально отыграло
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

            val enabled = !item.used && !locked
            binding.root.isEnabled = enabled
            binding.root.alpha = if (enabled) 1f else 0.45f
        }
    }

    companion object {
        private const val PRESS_DELAY_MS = 120L

        private val DIFF = object : DiffUtil.ItemCallback<WriteTheWordLetterUi>() {
            override fun areItemsTheSame(oldItem: WriteTheWordLetterUi, newItem: WriteTheWordLetterUi) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WriteTheWordLetterUi, newItem: WriteTheWordLetterUi) =
                oldItem == newItem
        }
    }
}
