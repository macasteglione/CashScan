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
import com.example.reconocimiento_billetes.presentation.getLocalizedAudioResId
import com.google.android.material.tabs.TabLayout

/**
 * TutorialActivity muestra una secuencia de pantallas introductorias con audio y animaciones.
 * Permite navegar entre pantallas y realizar acciones como saltar, continuar o finalizar el tutorial.
 */
class TutorialActivity : AppCompatActivity() {

    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    private lateinit var tvSkip: TextView
    private lateinit var btnAnim: Animation
    private lateinit var gestureDetector: GestureDetector

    private var position = 0
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Método de inicialización de la actividad.
     * Configura los elementos de la vista, la navegación y la reproducción de audio.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        tabIndicator = findViewById(R.id.tab_indicator)
        tvSkip = findViewById(R.id.tv_skip)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)

        val mList = listOf(
            ScreenItem(
                getString(R.string.intro1Title),
                getString(R.string.intro1),
                getLocalizedAudioResId(this, "intro_1")
            ),
            ScreenItem(
                getString(R.string.intro2Title),
                getString(R.string.intro2),
                getLocalizedAudioResId(this, "intro_2")
            ),
            ScreenItem(
                getString(R.string.intro3Title),
                getString(R.string.intro3),
                getLocalizedAudioResId(this, "intro_3")
            ),
            ScreenItem(
                getString(R.string.historialDeBilletes),
                getString(R.string.intro4),
                getLocalizedAudioResId(this, "intro_4")
            ),
            ScreenItem(
                getString(R.string.intro5Title),
                getString(R.string.intro5),
                getLocalizedAudioResId(this, "intro_5")
            )
        )

        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager.adapter = introViewPagerAdapter
        tabIndicator.setupWithViewPager(screenPager)

        // Reproduce el audio para la primera pantalla
        playAudioForCurrentScreenItem(mList[0])

        // Configura el comportamiento al cambiar de pantalla en el ViewPager
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

    /**
     * Reproduce el audio correspondiente al ítem actual del tutorial.
     * @param screenItem El ítem que contiene la información de la pantalla actual.
     */
    private fun playAudioForCurrentScreenItem(screenItem: ScreenItem) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, screenItem.audio)
        mediaPlayer?.start()
    }

    /**
     * Configura la interfaz al llegar a la última pantalla del tutorial.
     * Cambia la visibilidad de los botones y oculta el indicador de tabulador.
     */
    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        tvSkip.visibility = View.INVISIBLE
        tabIndicator.visibility = View.INVISIBLE
        btnGetStarted.animation = btnAnim
    }

    /**
     * Navega hacia la actividad principal (IntroActivity) cuando se finaliza el tutorial.
     */
    private fun backToIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad actual
    }

    /**
     * Libera los recursos del reproductor de medios cuando la actividad es destruida.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}