package com.sleeplessdog.matchthewords.game.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.CategoryUi

class FeaturedCategoriesController(
    private val chipGroup: FlexboxLayout,
    private val onToggle: (String) -> Unit
) {
    fun render(list: List<CategoryUi>) {
        chipGroup.removeAllViews()
        list.forEach { item ->
            val chip = createCategoryChip(chipGroup, item)
            chip.isChecked = item.isSelected
            chip.setOnClickListener { onToggle(item.key) }
            chipGroup.addView(chip)
        }
    }

    fun createCategoryChip(parent: ViewGroup, item: CategoryUi): Chip {
        val ctx = parent.context
        val chip =
            LayoutInflater.from(ctx).inflate(R.layout.view_category_chip, parent, false) as Chip

        chip.text = item.title
        chip.isCheckable = true
        chip.tag = item.key
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            ctx, R.color.selector_options_button_bg
        )
        if (item.iconRes != 0) {
            chip.chipIcon = AppCompatResources.getDrawable(ctx, item.iconRes)
            chip.isChipIconVisible = true
        } else {
            chip.isChipIconVisible = false
        }

        return chip
    }
}
