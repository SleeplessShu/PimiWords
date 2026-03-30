package com.sleeplessdog.pimi.gameSelect

import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectCardLongBinding

class GameOptionViewLong @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding =
        GameSelectCardLongBinding.inflate(LayoutInflater.from(context), this, true)

    private var iconNormal: Int = R.drawable.ic_launcher_foreground
    private var iconSelected: Int = R.drawable.ic_launcher_foreground

    init {
        setSelectedState(false)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.rootCard.setOnClickListener(l)
    }

    fun setup(title: String, iconNormalRes: Int, iconSelectedRes: Int) {
        binding.title.text = title
        iconNormal = iconNormalRes
        iconSelected = iconSelectedRes

        val normal = ContextCompat.getDrawable(context, iconNormalRes)!!
        val active = ContextCompat.getDrawable(context, iconSelectedRes)!!
        val sld = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), active)
            addState(intArrayOf(android.R.attr.state_pressed), active)
            addState(intArrayOf(), normal)
        }
        binding.icon.setImageDrawable(sld)
        binding.icon.isDuplicateParentStateEnabled = true
    }

    fun setSelectedState(isSelected: Boolean) {
        binding.rootCard.isSelected = isSelected
        binding.title.isSelected = isSelected
        binding.icon.isSelected = isSelected
        binding.icon.setImageResource(if (isSelected) iconSelected else iconNormal)
    }
}