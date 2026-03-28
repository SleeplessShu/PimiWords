package com.sleeplessdog.pimi.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.ActivityMainBinding
import com.sleeplessdog.pimi.dictionary.dictionary_screen.DatabaseSyncController
import com.sleeplessdog.pimi.utils.ConstantsPaths.TAG_MAIN_ACTIVITY
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.getKoin


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.isVisible = false

        lifecycleScope.launch {
            try {

                Log.d(TAG_MAIN_ACTIVITY, "Firebase auth check...")

                val currentUser = Firebase.auth.currentUser
                val isGoogleUser =
                    currentUser?.providerData?.any { it.providerId == "google.com" } == true

                if (currentUser == null) {
                    // Никого нет — анонимный для доступа к глобальной БД
                    Firebase.auth.signInAnonymously().await()
                    Log.d(TAG_MAIN_ACTIVITY, "Firebase anonymous auth success")
                } else if (isGoogleUser) {
                    // Уже залогинен через Google — ничего не трогаем
                    Log.d(
                        TAG_MAIN_ACTIVITY, "Already signed in with Google, uid=${currentUser.uid}"
                    )
                } else {
                    // Анонимный с прошлого раза — оставляем как есть
                    Log.d(TAG_MAIN_ACTIVITY, "Anonymous user exists, uid=${currentUser.uid}")
                }

                val syncController: DatabaseSyncController = getKoin().get()
                syncController.prepareGlobalDatabaseOnly()

                Log.d(TAG_MAIN_ACTIVITY, "Global DB prepared")

            } catch (e: Exception) {
                Log.e(TAG_MAIN_ACTIVITY, "Global DB init error: ${e.message}")
            }
        }
        setupNavigation()
    }

    /**
     * Метод для настройки навигации.
     * Вызывается только когда мы уверены, что файл БД существует.
     */
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment

        if (navHostFragment == null) {
            Log.e(
                TAG_MAIN_ACTIVITY,
                "initAppAfterDbReady: fragment_container is not a NavHostFragment or is null"
            )
            return
        }

        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        setBottomNavVisibility(true)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.gameFragment -> {
                    setBottomNavVisibility(false)
                }

                else -> {
                    setBottomNavVisibility(true)
                }
            }
        }
    }

    /**
     * Публичный метод для управления видимостью bottom меню из фрагментов
     */
    fun setBottomNavVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.isVisible = isVisible
    }

    fun getBottomNavHeight(): Int {
        return binding.bottomNavigationView.height
    }
}
