package com.sleeplessdog.matchthewords.utils

import com.sleeplessdog.matchthewords.game.presentation.models.OneOfFourQuestion
import com.sleeplessdog.matchthewords.game.presentation.models.TfQuestionUi
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_AVAILABLE_INPUT
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_VARIANTS_COUNT_M8
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_VARIANTS_COUNT_OOF
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_VARIANTS_COUNT_TOF
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.WRONG_ANSWERS
import kotlin.random.Random

class ShuffleFunctions {

    //----------------------match8----------------------
    fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= MIN_VARIANTS_COUNT_M8) return input
        val secondWords = input.map { it.second }.shuffled(Random(System.currentTimeMillis()))
        return input.mapIndexed { index, pair ->
            pair.first to secondWords[index]
        }
    }

    //----------------------trueFalse----------------------
    private fun derangedCopy(input: List<Word>): List<Word> {
        if (input.size <= MIN_AVAILABLE_INPUT) return input

        val indices = input.indices.toMutableList()
        do indices.shuffle() while (indices.any { i -> indices[i] == i })
        return indices.map { input[it] }
    }

    fun buildTrueFalseSetOnce(input: List<Pair<Word, Word>>,
                              shuffleQuestionsOrder: Boolean = true): List<TfQuestionUi> {
        if (input.isEmpty()) return emptyList()

        val mid = input.size / MIN_VARIANTS_COUNT_TOF
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
            if (extended.size >= MIN_VARIANTS_COUNT_TOF) {
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

        val result = (correctQs + wrongQs)
        return if (shuffleQuestionsOrder) result.shuffled() else result
    }

    //----------------------onOf4----------------------
    fun makeOneOfFourQuestion(
        available: List<Pair<Word, Word>>,
        rnd: Random = Random.Default
    ): OneOfFourQuestion? {

        var result: OneOfFourQuestion? = null

        if (available.size >= MIN_VARIANTS_COUNT_OOF) {

            val baseIdx = rnd.nextInt(available.size)
            val base = available[baseIdx]

            val otherIdxs = (available.indices - baseIdx)
                .shuffled(rnd)
                .take(WRONG_ANSWERS)

            if (otherIdxs.size == WRONG_ANSWERS) {

                val wrongPairs = otherIdxs.map { available[it] }

                val consumed = (listOf(base) + wrongPairs)
                    .map { it.first.id }
                    .toSet()

                val options = (wrongPairs.map { it.second } + base.second)
                    .shuffled(rnd)

                result = OneOfFourQuestion(
                    originalFirst = base.first,
                    optionsSecond = options,
                    correctSecondId = base.second.id,
                    consumedFirstIds = consumed
                )
            }
        }

        return result
    }
}
