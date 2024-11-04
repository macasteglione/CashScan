package com.example.reconocimiento_billetes

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class IntroActivity : AppCompatActivity() {

    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    private var position = 0
    private lateinit var btnAnim: Animation
    private lateinit var tvSkip: TextView
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (restorePrefData()) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_intro)

        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        tvSkip = findViewById(R.id.tv_skip)

        val mList = listOf(
            ScreenItem(
                "¡Bienvenido!",
                "Esta aplicacion está diseñada para ayudarte a identificar y organizar " +
                        "billetes de manera rápida y sencilla. Antes de que comiences, " +
                        "me gustaría mostrarte cómo navegar en la aplicación usando solo " +
                        "gestos, para que puedas aprovechar todas sus funciones de forma intuitiva. " +
                        "Deslize a la izquierda para continuar.",
                R.raw.intro_1
            ),
            ScreenItem(
                "Escaner de Billetes",
                "¡Muy bien! En Cash Scan puedes escanear un billete en tiempo real " +
                        "usando la cámara de su celular. Cuando sientas que el celular vibre, " +
                        "te diré el valor del billete. Cada vez que desees acceder a esta " +
                        "herramienta, simplemente desliza el dedo hacia la derecha en el menú " +
                        "principal. Deslize a la izquierda para continuar.",
                R.raw.intro_2
            ),
            ScreenItem(
                "Historial de Billetes",
                "Además, puedes acceder al historial de billetes escaneados, donde " +
                        "encontraras el valor y la fecha de cada escaneo. Para abrir el " +
                        "historial, desliza el dedo hacia la izquierda en el menú principal. " +
                        "Si deseas borrar el historial, solo toca la pantalla cinco veces. Desde " +
                        "el historial también puedes compartir un archivo de texto con todos los " +
                        "billetes que has escaneado. Deslize a la izquierda para continuar.",
                R.raw.intro_3
            ),
            ScreenItem(
                "¡Listo!",
                "¡Genial! Ahora conoces cómo navegar entre las herramientas de esta " +
                        "aplicación. Para retroceder en cualquier momento, simplemente desliza " +
                        "el dedo hacia la izquierda. Si alguna vez necesitas un repaso, puedes " +
                        "volver al tutorial desde el menú principal. ¡Ya estamos preparados! " +
                        "Deslize a la izquierda para empezar.",
                R.raw.intro_4
            )
        )

        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager.adapter = introViewPagerAdapter
        tabIndicator.setupWithViewPager(screenPager)

        screenPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                playAudioForCurrentScreenItem(mList[position])
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        btnNext.setOnClickListener {
            position = screenPager.currentItem
            if (position < mList.size - 1) {
                position++
                screenPager.currentItem = position
            }

            if (position == mList.size - 1) loadLastScreen()
        }

        tabIndicator.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == mList.size - 1) loadLastScreen()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnGetStarted.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            savePrefsData()
            finish()
        }

        tvSkip.setOnClickListener {
            screenPager.currentItem = mList.size
        }
    }

    private fun playAudioForCurrentScreenItem(screenItem: ScreenItem) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, screenItem.audio)
        mediaPlayer?.start()
    }

    private fun restorePrefData(): Boolean {
        val pref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun savePrefsData() {
        val pref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        tvSkip.visibility = View.INVISIBLE
        tabIndicator.visibility = View.INVISIBLE
        btnGetStarted.animation = btnAnim
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}