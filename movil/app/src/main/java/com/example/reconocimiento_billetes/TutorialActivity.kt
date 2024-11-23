package com.example.reconocimiento_billetes

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.reconocimiento_billetes.domain.ScreenItem
import com.example.reconocimiento_billetes.presentation.IntroViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class TutorialActivity : AppCompatActivity() {

    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    private var position = 0
    private lateinit var btnAnim: Animation
    private lateinit var tvSkip: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                "¡Muy bien! En Cash Scan, puedes identificar un billete usando la " +
                        "cámara de tu celular. Para comenzar el escaneo, presiona el botón de " +
                        "bajar volumen. Cuando sientas que el celular vibra, te diré el valor del " +
                        "billete detectado. Si deseas escanear otro billete, simplemente toca " +
                        "cualquier parte de la pantalla y repite los pasos. Cada vez que quieras " +
                        "acceder a esta herramienta, desliza tu dedo hacia la derecha desde el " +
                        "menú principal. Desliza hacia la izquierda para continuar.",
                R.raw.intro_2
            ),
            ScreenItem(
                "Historial de Billetes",
                "Además, puedes acceder al historial de billetes escaneados, donde " +
                        "encontraras el valor y la fecha de cada escaneo. Para abrir el historial, " +
                        "desliza el dedo hacia la izquierda en el menú principal. Si deseas borrar " +
                        "el historial, solo toca la pantalla cinco veces. Desde el historial " +
                        "también puedes compartir un archivo de texto con todos los billetes que " +
                        "has escaneado simplemente deslizando hacia la derecha. " +
                        "Deslize a la izquierda para continuar.",
                R.raw.intro_3
            ),
            ScreenItem(
                "¡Listo!",
                "¡Genial! Ahora conoces cómo navegar entre las herramientas de esta " +
                        "aplicación. Para retroceder en cualquier momento, simplemente desliza " +
                        "el dedo hacia la izquierda. Si alguna vez necesitas un repaso, puedes " +
                        "volver al tutorial desde el menú principal deslizando hacia arriba." +
                        "Si quieres compartir tu opinión sobre la aplicación o sugerir nuevas " +
                        "herramientas, contáctanos deslizando hacia abajo. " +
                        "¡Ya estamos preparados! Toque dos veces para empezar.",
                R.raw.intro_4
            )
        )

        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager.adapter = introViewPagerAdapter
        tabIndicator.setupWithViewPager(screenPager)

        playAudioForCurrentScreenItem(mList[0])

        screenPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                this@TutorialActivity.position = position
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
            backToIntroActivity()
        }

        tvSkip.setOnClickListener {
            screenPager.currentItem = mList.size
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (position == mList.size - 1) {
                    backToIntroActivity()
                    return true
                }

                return false
            }
        })

        screenPager.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun playAudioForCurrentScreenItem(screenItem: ScreenItem) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, screenItem.audio)
        mediaPlayer?.start()
    }

    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        tvSkip.visibility = View.INVISIBLE
        tabIndicator.visibility = View.INVISIBLE
        btnGetStarted.animation = btnAnim
    }

    private fun backToIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}