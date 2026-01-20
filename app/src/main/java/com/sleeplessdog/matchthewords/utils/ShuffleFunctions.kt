package com.sleeplessdog.matchthewords.utils

import com.sleeplessdog.matchthewords.game.presentation.models.OneOfFourQuestion
import com.sleeplessdog.matchthewords.game.presentation.models.TfQuestionUi
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import kotlin.random.Random

class ShuffleFunctions {

    //----------------------match8----------------------
    fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= 1) return input
        val secondWords = input.map { it.second }.shuffled(Random(System.currentTimeMillis()))
        return input.mapIndexed { index, pair ->
            pair.first to secondWords[index]
        }
    }

    //----------------------trueFalse----------------------
    private fun derangedCopy(input: List<Word>): List<Word> {
        if (input.size <= 1) return input
        val indices = input.indices.toMutableList()
        // перемешиваем, пока не совпадает позиция (индекс i → тот же элемент)
        do indices.shuffle() while (indices.any { i -> indices[i] == i })
        return indices.map { input[it] }
    }

    fun buildTrueFalseSetOnce(input: List<Pair<Word, Word>>, shuffleQuestionsOrder: Boolean = true): List<TfQuestionUi> {
        if (input.isEmpty()) return emptyList()

        val mid = input.size / 2
        val firstHalf = input.subList(0, mid)
        val secondHalf = input.subList(mid, input.size)

        // 1) Первая половина — правильные
        val correctQs = firstHalf.map { (w, t) ->
            TfQuestionUi(word = w, shownTranslation = t, isCorrect = true)
        }

        // 2) Вторая половина — делаем неверные связывания внутри половины
        val secondTranslations = secondHalf.map { it.second }
        val wrongTranslations = if (secondTranslations.size >= 2) {
            derangedCopy(secondTranslations)
        } else {
            // если во второй половине 1 элемент — дерранжмент невозможен.
            // Перетащим 1 элемент из первой половины, чтобы было >=2.
            val extended = (firstHalf.takeIf { it.isNotEmpty() }?.map { it.second } ?: emptyList()) + secondTranslations
            if (extended.size >= 2) {
                derangedCopy(extended).takeLast(secondTranslations.size)
            } else {
                // крайний случай — покажем как правильный (иначе не из чего собрать wrong)
                secondTranslations
            }
        }

        val wrongQs = secondHalf.mapIndexed { i, (w, correctT) ->
            val shownT = wrongTranslations.getOrNull(i) ?: correctT
            TfQuestionUi(word = w, shownTranslation = shownT, isCorrect = shownT.id == correctT.id)
        }

        // 3) Склеиваем и (опционально) перемешиваем порядок вопросов
        val result = (correctQs + wrongQs)
        return if (shuffleQuestionsOrder) result.shuffled() else result
    }

    //----------------------onOf4----------------------
    fun makeOneOfFourQuestion(
        available: List<Pair<Word, Word>>,
        rnd: Random = Random.Default
    ): OneOfFourQuestion? {
        if (available.size < 4) return null

        // базовая пара
        val baseIdx = rnd.nextInt(available.size)
        val base = available[baseIdx]

        // выбираем 3 ложных пары (по second)
        val otherIdxs = (available.indices - baseIdx).shuffled(rnd).take(3)
        if (otherIdxs.size < 3) return null
        val wrongPairs = otherIdxs.map { available[it] }

        // всё, что участвовало в вопросе, нужно будет исключить по first.id
        val consumed = (listOf(base) + wrongPairs).map { it.first.id }.toSet()

        // 4 варианта переводов (3 ложных + 1 правильный), перемешиваем
        val options = (wrongPairs.map { it.second } + base.second).shuffled(rnd)

        return OneOfFourQuestion(
            originalFirst = base.first,
            optionsSecond = options,
            correctSecondId = base.second.id,
            consumedFirstIds = consumed
        )
    }
}