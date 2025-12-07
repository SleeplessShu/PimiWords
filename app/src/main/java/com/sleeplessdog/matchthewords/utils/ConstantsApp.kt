package com.sleeplessdog.matchthewords.utils

object ConstantsApp {
    const val FADE_IN_DURATION_MS = 200L
    const val FULL_ALPHA = 1f
    const val HALF_ALPHA = 0.4f
    const val EMPTY_ALPHA = 0f
    const val ZERO_SCALE = 0f
    const val FULL_SCALE = 1f
    const val INVALID_ID = -1
    const val FEATURED_LIMIT = 8

    //card preview
    const val CARD_PREVIEW_TRANSLATION_Y = 24f
    const val CARD_PREVIEW_SCALE = 0.98f

    //swipe animation
    const val SWIPE_VERTICAL_FACTOR = 0.35f
    const val SWIPE_ROTATION_DEGREES = 35f
    const val CARD_SWAP_DURATION_MS = 220L
    const val RESULT_HIGHLIGHT_ALPHA = 0.6f
    const val COLOR_MAX_CHANNEL = 255
    const val ANIMATION_DURATION_FOREGROUND = 150L
    const val ANIMATION_DURATION_BACKGROUND = 200L
    const val TOPICS_MENU_CONTENT_OFFSET_Y = 40f


    const val DATE_PATTERN = "yyyy-MM-dd"
    const val SCORE_LENGTH = 9
    const val SCORE_FILL_CHAR = '0'
    const val ZERO_STRING = "0"

    const val START_LIVES = 3
    const val DEFAULT_DIFFICULT_LEVEL = 18
    const val MAX_LIVES = 3

    // SegmentedProgressBar
    const val DEFAULT_SEGMENTS = 10
    const val MIN_SEGMENTS = 1
    const val MAX_SEGMENTS = 100

    const val DEFAULT_PROGRESS = 0f
    const val MAX_PROGRESS = 1f
    const val CORNER_DEFAULT = 2f

    const val DEFAULT_ANIMATION_DURATION_MS = 300L

    const val DEFAULT_SEGMENT_DP = 12f
    const val DEFAULT_GAP_DP = 6f
    const val DEFAULT_BAR_HEIGHT_DP = 10f

    const val BG_COLOR_DEFAULT = 0xFF2F3234.toInt()
    const val FG_COLOR_DEFAULT = 0xFFE8DFCB.toInt()

    // hearts
    const val MAX_HEARTS = 3
    const val MIN_HEARTS = 0

    const val HEART_SHOW_DURATION_MS = 250L
    const val HEART_HIDE_DURATION_MS = 200L

    const val TOUCH_EXPAND_FACTOR = 6f

    const val SWIPE_DRAG_LIMIT = 280f
    const val SWIPE_COMMIT_THRESHOLD = 140f
    const val SWIPE_MAX_ROTATION = 15f
    const val SWIPE_VERTICAL_TRANSLATION_FACTOR = 0.12f
    const val SWIPE_RESET_DURATION_MS = 180L
}
