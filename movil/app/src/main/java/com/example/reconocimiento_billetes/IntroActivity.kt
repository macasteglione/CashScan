package com.example.reconocimiento_billetes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Actividad inicial de la aplicación que maneja la pantalla de bienvenida (Splash Screen).
 * Esta actividad verifica si es la primera vez que el usuario abre la aplicación.
 * Si es la primera vez, muestra el tutorial, de lo contrario, redirige directamente
 * a la actividad principal.
 */
class IntroActivity : AppCompatActivity() {

    /**
     * Método que se ejecuta cuando la actividad es creada.
     * Muestra el Splash Screen, verifica si el tutorial ya fue mostrado,
     * y redirige a la actividad correspondiente (Tutorial o Main).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Muestra el Splash Screen durante 1 segundo
        Thread.sleep(1000)
        installSplashScreen()

        if (isTutorialShown()) {
            navigateToMainActivity()
        } else {
            markTutorialAsShown()
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        finish()
    }

    /**
     * Verifica si el tutorial ya ha sido mostrado en sesiones anteriores.
     * Utiliza SharedPreferences para almacenar el estado de esta preferencia.
     *
     * @return `true` si el tutorial ya fue mostrado, `false` si no lo ha sido.
     */
    private fun isTutorialShown(): Boolean {
        val sharedPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        return sharedPrefs.getBoolean("isIntroOpened", false)
    }

    /**
     * Inicia la actividad principal de la aplicación (MainActivity).
     */
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * Marca en SharedPreferences que el tutorial ha sido mostrado.
     */
    private fun markTutorialAsShown() {
        val sharedPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }
}