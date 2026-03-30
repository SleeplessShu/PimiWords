package com.sleeplessdog.pimi.games.presentation.states

enum class HeartState {
    APPEAR_BRIGHT_ORANGE,   // Появление 3 сердечек
    BRIGHT_TO_MEDIUM,       // Из 3 сердечек в 2
    DEAD_BRIGHT_ORANGE,     // Исчезновение яркого сердечка
    MEDIUM_TO_DARK,         // Из 2 сердечек в 1
    DEAD_MEDIUM,            // Исчезновение среднего сердечка
    DARK_TO_MEDIUM,         // Из 1 сердечка в 2
    APPEAR_MEDIUM_ORANGE,   // Появление второго сердечка
    MEDIUM_TO_BRIGHT,       // Из 2 сердечек в 3
    DEAD_DARK               // Исчезновение темного сердечка
}
