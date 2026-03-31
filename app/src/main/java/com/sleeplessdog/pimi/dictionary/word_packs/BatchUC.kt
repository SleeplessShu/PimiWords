package com.sleeplessdog.pimi.dictionary.word_packs

import com.google.common.reflect.TypeToken
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.games.data.repository.GroupsRepository
import com.sleeplessdog.pimi.games.data.repository.WordsRepository
import com.sleeplessdog.pimi.utils.ConstantsPaths.WORD_PACKS_PATH
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GetWordPacksUC(
    private val storage: FirebaseStorage,
) {
    suspend operator fun invoke(): Result<List<WordPackMeta>> {
        return try {
            val indexRef = storage.reference
                .child(WORD_PACKS_PATH)

            val bytes = indexRef.getBytes(1024 * 1024).await()
            val json = String(bytes)

            val type = object : TypeToken<List<WordPackMeta>>() {}.type
            val allPacks: List<WordPackMeta> = Gson().fromJson(json, type)

            Result.success(allPacks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class InstallWordPackUC(
    private val storage: FirebaseStorage,
    private val groupsRepository: GroupsRepository,
    private val wordsRepository: WordsRepository,
    private val appPrefs: AppPrefs,
) {
    suspend operator fun invoke(fileName: String): Result<Unit> {
        return try {
            val lang = appPrefs.getUiLanguage()
            val ref = storage.reference.child("word_packs/${fileName}")
            val bytes = ref.getBytes(5 * 1024 * 1024).await()
            val json = String(bytes)

            val fullPack: WordPack = Gson().fromJson(json, WordPack::class.java)

            val groupKey = UUID.randomUUID().toString()
            groupsRepository.createUserGroup(
                groupKey,
                fullPack.nameForLanguage(lang)
            )

            fullPack.words.forEach { entry ->
                wordsRepository.addWordPackEntry(groupKey, entry)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
