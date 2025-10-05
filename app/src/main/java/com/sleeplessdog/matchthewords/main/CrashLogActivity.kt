package com.sleeplessdog.matchthewords.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.databinding.ActivityCrashLogBinding
import java.io.File

class CrashLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Crash log"

        binding.btnRefresh.setOnClickListener { loadLog() }
        binding.btnShare.setOnClickListener { shareLog() }
        binding.btnDelete.setOnClickListener { deleteLog() }

        loadLog()
    }

    private fun loadLog() {
        val file = File(App.crashFilePath())
        if (file.exists()) {
            binding.tvLog.text = file.readText()
            binding.emptyView.root.visibility = android.view.View.GONE
            binding.containerButtons.alpha = 1f
        } else {
            binding.tvLog.text = ""
            binding.emptyView.root.visibility = android.view.View.VISIBLE
            binding.containerButtons.alpha = 0.4f
        }
        // сбрасываем флаг — лог уже просмотрен
        App.clearCrashFlag()
    }

    private fun shareLog() {
        val file = File(App.crashFilePath())
        if (!file.exists()) return
        val text = file.readText()
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Crash log")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(sendIntent, "Share crash log"))
    }

    private fun deleteLog() {
        val file = File(App.crashFilePath())
        if (file.exists()) file.delete()
        loadLog()
    }
}