package com.example.reconocimiento_billetes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        installSplashScreen()

        if (restorePrefData()) startMainActivity()
        else {
            savePrefsData()
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        finish()
    }

    private fun restorePrefData(): Boolean {
        val pref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun savePrefsData() {
        val pref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }
}
