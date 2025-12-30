package com.sleeplessdog.matchthewords.game.presentation.parentModels

enum class HeartState {
    FAST_DARK_TO_MEDIUM,    // 1 → 2: темнооранжевый быстро меняется на среднеоранжевый
    APPEAR_MEDIUM_ORANGE,   // Появление среднего оранжевого сердца (get_heart_to_2)
    MEDIUM_TO_BRIGHT,       // 2 → 3: среднеоранжевый плавно становится яркооранжевым
    APPEAR_BRIGHT_ORANGE    // Появление яркого оранжевого сердца (get_heart_to_3)
}