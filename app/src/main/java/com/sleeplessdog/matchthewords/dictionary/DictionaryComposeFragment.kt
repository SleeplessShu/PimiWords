package com.sleeplessdog.matchthewords.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

class DictionaryComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DictionaryScreen(
                    myGroups = listOf(
                        WordMyGroup(
                            "Приветствия", listOf("Привет", "Здравствуй", "Добрый день")
                        ),
                        WordMyGroup(
                            "Птички", listOf("Попугай", "Аист", "Сокол", "Воробей", "Чайка")
                        ),
                        WordMyGroup(
                            "Рыбы", listOf("Осетр")
                        )
                    ),
                    standardGroups = listOf(
                        WordStandardGroup("Путешествия", listOf("Отель")),
                        WordStandardGroup("Дом", listOf("Кровать", "Стол")),
                        WordStandardGroup(
                            "Работа", listOf("Зарплата", "График", "Коллега", "Отпуск", "Премия")
                        ),
                        WordStandardGroup("Птицы", listOf("Голубь"))
                    ),
                    bufferWords = listOf("apple", "dog", "cat")
                )
            }
        }
    }
}