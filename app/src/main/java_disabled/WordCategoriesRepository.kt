package com.sleeplessdog.pimi.game.domain.repositories

import com.sleeplessdog.pimi.backend.domain.models.WordGroup

interface WordCategoriesRepository {
    /** Избранные для главного (ограничение по количеству). */
    fun observeFeatured(limit: Int): kotlinx.coroutines.flow.Flow<List<WordGroup>>

    /** Все категории, отсортированные: user → default. */
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<WordGroup>>

    /** В виде блоков для нижнего листа. */
    fun observeAllGrouped(): kotlinx.coroutines.flow.Flow<CategoriesGrouped>

    /** Мгновенно переключить выбор конкретной категории. */
    suspend fun toggleSelection(key: String)

    /** Сохранить выбор пакетно (например, из bottom sheet). */
    suspend fun saveSelection(selectedKeys: Set<String>)

    /** Создать/обновить пользовательскую категорию. */
    suspend fun upsertUserCategory(
        key: String,
        titleKey: String,
        iconKey: String,
        orderInBlock: Int = 0,
    )

    /** Удалить пользовательскую категорию. */
    suspend fun deleteUserCategory(key: String)
}

data class CategoriesGrouped(
    val user: List<WordGroup>,
    val defaults: List<WordGroup>,
)
