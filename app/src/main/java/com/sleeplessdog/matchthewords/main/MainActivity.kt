package com.sleeplessdog.matchthewords.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.ActivityMainBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.DictionaryDatabaseController
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Тэг для логов
    private val TAG = "MainActivity_DB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализация Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Скрываем навигацию и показываем загрузку (если есть ProgressBar в макете)
        binding.bottomNavigationView.isVisible = false

        // 2. Запуск процесса подготовки БД в корутине
        lifecycleScope.launch {
            try {
                // Анонимный вход в Firebase для доступа к Storage
                Log.d(TAG, "Попытка входа в Firebase...")
                Firebase.auth.signInAnonymously().await()
                Log.d(TAG, "Вход выполнен успешно")

                // Инициализация контроллера и загрузка базы
                val dbLoader = DictionaryDatabaseController(this@MainActivity)
                val result = dbLoader.prepareDatabase()

                if (result.isSuccess) {
                    Log.d(TAG, "База данных успешно подготовлена")
                    initAppAfterDbReady()
                } else {
                    Log.e(TAG, "Ошибка при подготовке БД: ${result.exceptionOrNull()}")
                    // Здесь можно вызвать initAppAfterDbReady() всё равно,
                    // если в prepareDatabase настроен fallback на assets
                    initAppAfterDbReady()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Критическая ошибка инициализации: ${e.message}")
                // Фоллбэк: пытаемся запустить приложение, надеясь на локальную копию
                initAppAfterDbReady()
            }
        }

        // Проверка на падения из предыдущих сессий
        if (App.hasCrash()) {
            startActivity(Intent(this, CrashLogActivity::class.java))
        }
    }

    /**
     * Метод для настройки навигации.
     * Вызывается только когда мы уверены, что файл БД существует.
     */
    private fun initAppAfterDbReady() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Настройка Bottom Navigation
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.isVisible = true

        // Слушатель изменения экранов
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.gameFragment -> {
                    binding.bottomNavigationView.isVisible = false
                }

                else -> {
                    binding.bottomNavigationView.isVisible = true
                }
            }
        }
    }

    /**
     * Публичный метод для управления видимостью меню из фрагментов
     */
    fun setBottomNavVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.isVisible = isVisible
    }
}