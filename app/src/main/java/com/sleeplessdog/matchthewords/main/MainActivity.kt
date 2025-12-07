package com.sleeplessdog.matchthewords.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.ActivityMainBinding
import kotlin.jvm.java

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        if (App.hasCrash()) {
            startActivity(Intent(this, CrashLogActivity::class.java))
        }
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
}