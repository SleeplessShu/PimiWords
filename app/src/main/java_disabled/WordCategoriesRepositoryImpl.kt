package com.sleeplessdog.pimi.game.data.repositories

import com.sleeplessdog.pimi.backend.domain.models.toDomain
import com.sleeplessdog.pimi.game.data.WordCategoryEntity
import com.sleeplessdog.pimi.game.data.database.WordCategoryDao
import com.sleeplessdog.pimi.game.domain.models.WordsGroupsList
import com.sleeplessdog.pimi.game.domain.repositories.CategoriesGrouped
import com.sleeplessdog.pimi.game.domain.repositories.WordCategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WordCategoriesRepositoryImpl(
    private val dao: WordCategoryDao,
) : WordCategoriesRepository {

    override fun observeFeatured(limit: Int) =
        dao.observeFeatured(limit).map { it.map(WordCategoryEntity::toDomain) }

    override fun observeAll() =
        dao.observeAll().map { it.map(WordCategoryEntity::toDomain) }

    override fun observeAllGrouped(): Flow<CategoriesGrouped> =
        dao.observeAll().map { list ->
            val domain = list.map(WordCategoryEntity::toDomain)
            CategoriesGrouped(
                user = domain.filter { it.isUser },
                defaults = domain.filter { !it.isUser }
            )
        }

    override suspend fun toggleSelection(key: String) {
        val all = dao.observeAll().first()
        val randomKey = WordsGroupsList.RANDOM.key

        val selected = all.filter { it.isSelected }.map { it.key }.toMutableSet()

        if (selected.contains(key)) {
            selected.remove(key)
        } else {
            selected.add(key)
        }

        val normalized: Set<String> =
            if (selected.isEmpty()) {
                setOf(randomKey)
            } else {
                selected - randomKey
            }

        dao.replaceSelection(normalized)
    }

    override suspend fun saveSelection(selectedKeys: Set<String>) {
        val randomKey = WordsGroupsList.RANDOM.key
        val normalized: Set<String> =
            if (selectedKeys.isEmpty()) {
                setOf(randomKey)
            } else {
                selectedKeys - randomKey
            }

        dao.replaceSelection(normalized)
    }

    override suspend fun upsertUserCategory(
        key: String,
        titleKey: String,
        iconKey: String,
        orderInBlock: Int,
    ) {
        dao.upsert(
            WordCategoryEntity(
                key = key,
                titleKey = titleKey,
                iconKey = iconKey,
                isSelected = true,
                isUser = true,
                orderInBlock = orderInBlock
            )
        )
    }

    override suspend fun deleteUserCategory(key: String) {
        dao.deleteUserCategory(key)
    }
}
