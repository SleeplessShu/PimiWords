package com.sleeplessdog.matchthewords.gameSelect.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.Language

class LanguageAdapter(
    private val onClick: (Language) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LangVH>() {

    private val items = mutableListOf<Language>()
    private var selected: Language? = null

    fun submit(langs: List<Language>, selected: Language?) {
        items.clear()
        items.addAll(langs)
        this.selected = selected
        notifyDataSetChanged()
    }

    fun setSelected(lang: Language) {
        selected = lang
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language_option, parent, false)
        return LangVH(v)
    }

    override fun onBindViewHolder(holder: LangVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LangVH(view: View) : RecyclerView.ViewHolder(view) {
        private val root = view.findViewById<ConstraintLayout>(R.id.root)
        private val flag = view.findViewById<ImageView>(R.id.ivFlagUi)
        private val title = view.findViewById<TextView>(R.id.tvTitle)

        fun bind(lang: Language) {
            root.isSelected = (lang == selected)
            title.text = lang.toTitle()
            flag.setImageResource(lang.toFlagSmallRes())
            root.setOnClickListener { onClick(lang) }
        }
    }
}

fun Language.toTitle(): String = when (this) {
    Language.ENGLISH -> "Английский"
    Language.SPANISH -> "Испанский"
    Language.RUSSIAN -> "Русский"
    Language.FRENCH  -> "Французский"
    Language.GERMAN  -> "Немецкий"
}

fun Language.toFlagSmallRes(): Int = when (this) {
    Language.ENGLISH -> R.drawable.ic_language_flag_british_s
    Language.SPANISH -> R.drawable.ic_language_flag_spanish_s
    Language.RUSSIAN -> R.drawable.ic_language_flag_russian_s
    Language.FRENCH  -> R.drawable.ic_language_flag_french_s
    Language.GERMAN  -> R.drawable.ic_language_flag_german_s
}

fun Language.toFlagLargeRes(): Int = when (this) {
    Language.ENGLISH -> R.drawable.ic_language_flag_british_l
    Language.SPANISH -> R.drawable.ic_language_flag_spanish_l
    Language.RUSSIAN -> R.drawable.ic_language_flag_russian_l
    Language.FRENCH  -> R.drawable.ic_language_flag_french_l
    Language.GERMAN  -> R.drawable.ic_language_flag_german_l
}

