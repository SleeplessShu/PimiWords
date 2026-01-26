package com.sleeplessdog.matchthewords.main

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
import com.sleeplessdog.matchthewords.game.presentation.controller.GlobalDatabaseController
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.TAG_MAIN_ACTIVITY
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.isVisible = false

        lifecycleScope.launch {
            try {
                // Анонимный вход в Firebase для доступа к Storage
                Log.d(TAG_MAIN_ACTIVITY, "Попытка входа в Firebase...")
                Firebase.auth.signInAnonymously().await()
                Log.d(TAG_MAIN_ACTIVITY, "Вход выполнен успешно")

                // Инициализация контроллера и загрузка базы
                val dbLoader = GlobalDatabaseController(this@MainActivity)
                val result = dbLoader.prepareDatabase()

                if (result.isSuccess) {
                    Log.d(TAG_MAIN_ACTIVITY, "База данных успешно подготовлена")
                } else {
                    Log.e(
                        TAG_MAIN_ACTIVITY, "Ошибка при подготовке БД: ${result.exceptionOrNull()}"
                    )

                }
            } catch (e: Exception) {
                Log.e(TAG_MAIN_ACTIVITY, "Критическая ошибка инициализации: ${e.message}")
                // Фоллбэк: пытаемся запустить приложение, надеясь на локальную копию

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
}
