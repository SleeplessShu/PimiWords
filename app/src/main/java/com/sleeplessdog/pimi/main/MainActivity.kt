package com.sleeplessdog.pimi.main

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.databinding.ActivityMainBinding
import com.sleeplessdog.pimi.dictionary.dictionary_screen.DatabaseSyncController
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.utils.ConstantsPaths.EXTRA_NAVIGATE_TO
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_UI_LANG
import com.sleeplessdog.pimi.utils.ConstantsPaths.NAV_SETTINGS
import com.sleeplessdog.pimi.utils.ConstantsPaths.PREFS_NAME
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.getKoin
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.isVisible = false

        lifecycleScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser

                if (currentUser == null) {
                    Firebase.auth.signInAnonymously().await()
                }

                val syncController: DatabaseSyncController = getKoin().get()
                syncController.prepareGlobalDatabaseOnly()

            } catch (e: Exception) {
            }
        }
        setupNavigation()

        intent.getStringExtra(EXTRA_NAVIGATE_TO)?.let { destination ->
            when (destination) {
                NAV_SETTINGS -> {
                    binding.bottomNavigationView.selectedItemId = R.id.settingsFragment
                }
            }
            intent.removeExtra(EXTRA_NAVIGATE_TO)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val langName = prefs.getString(KEY_UI_LANG, null)

        val language = langName?.let {
            runCatching { Language.valueOf(it) }.getOrNull()
        } ?: Language.ENGLISH

        val locale = language.toLocale()
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)

        super.attachBaseContext(context)
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
                ?: return

        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == navController.currentDestination?.id) return@setOnItemSelectedListener true

            val navOptions = androidx.navigation.navOptions {
                popUpTo(R.id.gameSelectFragment) {
                    saveState = false
                    inclusive = false
                }
                launchSingleTop = true
                restoreState = false
            }

            when (item.itemId) {
                R.id.gameSelectFragment -> {
                    navController.popBackStack(R.id.gameSelectFragment, false)
                }

                R.id.dictionaryComposeFragment -> {
                    navController.navigate(R.id.dictionaryComposeFragment, null, navOptions)
                }

                R.id.scoreFragment -> {
                    navController.navigate(R.id.scoreFragment, null, navOptions)
                }

                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment, null, navOptions)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            setBottomNavVisibility(destination.id != R.id.gameFragment)
        }

        setBottomNavVisibility(true)
    }

    fun setBottomNavVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.isVisible = isVisible
    }
}
