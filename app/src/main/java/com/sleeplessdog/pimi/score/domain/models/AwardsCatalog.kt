package com.sleeplessdog.pimi.score.domain.models

import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.score.models.AwardMeta

object AwardsCatalog {

    val all: List<AwardMeta> = listOf(

        AwardMeta(
            id = AwardId.MAMMOTH_HUNTER,
            title = R.string.award_mammoth_hunter_title,
            description = R.string.award_mammoth_hunter_desc,
            iconLocked = R.drawable.award_mamoth_off,
            iconUnlocked = R.drawable.award_mamoth_on
        ),

        AwardMeta(
            id = AwardId.RESPONSIBLE_CITIZEN,
            title = R.string.award_responsible_citizen_title,
            description = R.string.award_responsible_citizen_desc,
            iconLocked = R.drawable.award_responsibility_off,
            iconUnlocked = R.drawable.award_responsibility_on
        ),

        AwardMeta(
            id = AwardId.PERFECTIONIST,
            title = R.string.award_perfectionist_title,
            description = R.string.award_perfectionist_desc,
            iconLocked = R.drawable.award_perfectionist_off,
            iconUnlocked = R.drawable.award_perfectionist_on
        ),

        AwardMeta(
            id = AwardId.ALMOST,
            title = R.string.award_almost_title,
            description = R.string.award_almost_desc,
            iconLocked = R.drawable.award_almost_off,
            iconUnlocked = R.drawable.award_almost_on
        ),

        AwardMeta(
            id = AwardId.NOW_FOR_SURE,
            title = R.string.award_now_for_sure_title,
            description = R.string.award_now_for_sure_desc,
            iconLocked = R.drawable.award_for_sure_off,
            iconUnlocked = R.drawable.award_for_sure_on
        ),

        AwardMeta(
            id = AwardId.ALMOST_EXPERT,
            title = R.string.award_almost_expert_title,
            description = R.string.award_almost_expert_desc,
            iconLocked = R.drawable.award_almost_an_expert_off,
            iconUnlocked = R.drawable.award_almost_an_expert_on
        ),

        AwardMeta(
            id = AwardId.KNOWLEDGE_COLLECTOR,
            title = R.string.award_knowledge_collector_title,
            description = R.string.award_knowledge_collector_desc,
            iconLocked = R.drawable.collector_of_knowledge_off,
            iconUnlocked = R.drawable.collector_of_knowledge_on
        ),

        AwardMeta(
            id = AwardId.FOR_A_RAINY_DAY,
            title = R.string.award_for_a_rainy_day_title,
            description = R.string.award_for_a_rainy_day_desc,
            iconLocked = R.drawable.award_rainy_off,
            iconUnlocked = R.drawable.award_rainy_on
        ),

        AwardMeta(
            id = AwardId.MAYBE_USEFUL,
            title = R.string.award_maybe_useful_title,
            description = R.string.award_maybe_useful_desc,
            iconLocked = R.drawable.award_what_if_it_comes_in_handy_off,
            iconUnlocked = R.drawable.award_what_if_it_comes_in_handy_on
        ),

        AwardMeta(
            id = AwardId.I_LIVE_HERE,
            title = R.string.award_i_live_here_title,
            description = R.string.award_i_live_here_desc,
            iconLocked = R.drawable.award_live_here_off,
            iconUnlocked = R.drawable.award_live_here_on
        ),

        AwardMeta(
            id = AwardId.LITTLE_BUT_REGULAR,
            title = R.string.award_little_but_regular_title,
            description = R.string.award_little_but_regular_desc,
            iconLocked = R.drawable.award_a_little_bit_but_regularly_off,
            iconUnlocked = R.drawable.award_a_little_bit_but_regularly_on
        ),

        AwardMeta(
            id = AwardId.LAZY_PANDA,
            title = R.string.award_lazy_panda_title,
            description = R.string.award_lazy_panda_desc,
            iconLocked = R.drawable.award_panda_off,
            iconUnlocked = R.drawable.award_panda_on
        ),

        AwardMeta(
            id = AwardId.I_TRIED,
            title = R.string.award_i_tried_title,
            description = R.string.award_i_tried_desc,
            iconLocked = R.drawable.award_my_best_off,
            iconUnlocked = R.drawable.award_my_best_on
        ),

        AwardMeta(
            id = AwardId.WORD_BY_WORD,
            title = R.string.award_word_by_word_title,
            description = R.string.award_word_by_word_desc,
            iconLocked = R.drawable.award_word_for_word_off,
            iconUnlocked = R.drawable.award_word_for_word_on
        ),

        AwardMeta(
            id = AwardId.HONESTLY,
            title = R.string.award_honestly_title,
            description = R.string.award_honestly_desc,
            iconLocked = R.drawable.award_honestly_off,
            iconUnlocked = R.drawable.award_honestly_on
        ),

        AwardMeta(
            id = AwardId.SLOW_BUT_PRETTY,
            title = R.string.award_slow_but_pretty_title,
            description = R.string.award_slow_but_pretty_desc,
            iconLocked = R.drawable.award_slow_off,
            iconUnlocked = R.drawable.award_slow_on
        ),

        AwardMeta(
            id = AwardId.SELF_IMPROVEMENT,
            title = R.string.award_self_improvement_title,
            description = R.string.award_self_improvement_desc,
            iconLocked = R.drawable.award_improvement_off,
            iconUnlocked = R.drawable.award_improvement_on
        ),

        AwardMeta(
            id = AwardId.I_UNDERSTOOD,
            title = R.string.award_i_understood_title,
            description = R.string.award_i_understood_desc,
            iconLocked = R.drawable.award_expirience_off,
            iconUnlocked = R.drawable.award_expirience_on
        ),

        AwardMeta(
            id = AwardId.LEARNED_FROM_MISTAKES,
            title = R.string.award_learned_from_mistakes_title,
            description = R.string.award_learned_from_mistakes_desc,
            iconLocked = R.drawable.award_regular_off,
            iconUnlocked = R.drawable.award_regular_on
        ),

        AwardMeta(
            id = AwardId.FIGURED_OUT,
            title = R.string.award_figured_out_title,
            description = R.string.award_figured_out_desc,
            iconLocked = R.drawable.award_figured_it_out_off,
            iconUnlocked = R.drawable.award_figured_it_out_on
        )
    )
}
