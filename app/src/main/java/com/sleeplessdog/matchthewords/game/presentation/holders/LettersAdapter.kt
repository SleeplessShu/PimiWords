package com.sleeplessdog.matchthewords.game.presentation.holders

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
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
    private val onClick: (position: Int) -> Unit,
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
        holder.bind(getItem(position), locked, isSelected = false)
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

    @SuppressLint("ClickableViewAccessibility")
    class VH(
        private val binding: ItemLetterBinding,
        private val onOptimisticClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onPressHighlight()
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        onReleaseHighlight()
                    }
                }
                false // разрешаем обработку клика
            }

            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onOptimisticClick(bindingAdapterPosition)
                }
            }
        }


        // При нажатии (желтый фон, серый текст)
        fun onPressHighlight() {
            val context = binding.root.context
            val background = binding.root.background as? GradientDrawable
            background?.setColor(context.getColor(R.color.yellow_primary))
            binding.tLetter.setTextColor(context.getColor(R.color.gray_05))
        }

        // При отпускании (серый фон, серый текст — выделенный, к примеру)
        fun onReleaseHighlight() {
            val context = binding.root.context
            val background = binding.root.background as? GradientDrawable
            background?.setColor(context.getColor(R.color.gray_02))
            binding.tLetter.setTextColor(context.getColor(R.color.gray_05))
        }

        // При обычном состоянии до нажатия (серый фон 05, белый текст)
        fun clearHighlight() {
            val context = binding.root.context
            val background = binding.root.background as? GradientDrawable
            background?.setColor(context.getColor(R.color.gray_05))
            binding.tLetter.setTextColor(context.getColor(R.color.white))
        }

        fun onSelectedHighlight() {
            val context = binding.root.context
            val background = binding.root.background as? GradientDrawable
            background?.setColor(context.getColor(R.color.yellow_primary))
            binding.tLetter.setTextColor(context.getColor(R.color.white))
        }

        fun bind(item: WriteTheWordLetterUi, locked: Boolean, isSelected: Boolean) {
            val isSpace = item.char == ' '

            binding.iconSpace.visibility = if (isSpace) View.VISIBLE else View.GONE
            binding.tLetter.isInvisible = isSpace
            if (!isSpace) {
                binding.tLetter.text = item.char.toString()
            } else {
                binding.tLetter.text = ""
                binding.iconSpace.setImageResource(R.drawable.ic_space_bar)
            }

            val enabled = !item.used && !locked
            binding.root.isEnabled = enabled
            binding.root.alpha = if (enabled) 1f else 0.45f

            when {
                item.used -> onReleaseHighlight()
                isSelected -> onSelectedHighlight()
                else -> clearHighlight()
            }
        }
    }

    companion object {
        private const val PRESS_DELAY_MS = 120L

        private val DIFF = object : DiffUtil.ItemCallback<WriteTheWordLetterUi>() {
            override fun areItemsTheSame(
                oldItem: WriteTheWordLetterUi,
                newItem: WriteTheWordLetterUi,
            ) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: WriteTheWordLetterUi,
                newItem: WriteTheWordLetterUi,
            ) =
                oldItem == newItem
        }
    }
}
