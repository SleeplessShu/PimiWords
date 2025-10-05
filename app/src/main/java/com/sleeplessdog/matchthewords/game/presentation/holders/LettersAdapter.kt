package com.sleeplessdog.matchthewords.game.presentation.holders

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.button.MaterialButton
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordLetterUi
import com.google.android.material.R as MaterialR
import com.sleeplessdog.matchthewords.R

class LettersAdapter(
    private val onClick: (Int) -> Unit,
    private val context: Context
) : ListAdapter<WriteTheWordLetterUi, LettersAdapter.VH>(Diff()) {

    class VH(val btn: com.google.android.material.button.MaterialButton) :
        RecyclerView.ViewHolder(btn)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val ctx = parent.context
        val btn = MaterialButton(ctx, null, MaterialR.attr.materialButtonOutlinedStyle).apply {
            isAllCaps = false
            minWidth = 0
            minHeight = 0
            insetTop = 8
            insetBottom = 8

            layoutParams = FlexboxLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
        }

        return VH(btn)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.btn.text = item.char.toString()
        holder.btn.setTextColor(ContextCompat.getColor(context, R.color.day_background) )
        holder.btn.isEnabled = !item.used
        holder.btn.alpha = if (item.used) 0.4f else 1f
        holder.btn.setOnClickListener { onClick(holder.bindingAdapterPosition) }
    }

    private class Diff : DiffUtil.ItemCallback<WriteTheWordLetterUi>() {
        override fun areItemsTheSame(a: WriteTheWordLetterUi, b: WriteTheWordLetterUi) =
            a.id == b.id

        override fun areContentsTheSame(a: WriteTheWordLetterUi, b: WriteTheWordLetterUi) = a == b
    }
}
