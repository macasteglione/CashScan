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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
                getString(R.string.intro1Title),
                getString(R.string.intro1),
                R.raw.intro_1
            ),
            ScreenItem(
                getString(R.string.intro2Title),
                getString(R.string.intro2),
                R.raw.intro_2
            ),
            ScreenItem(
                getString(R.string.historialDeBilletes),
                getString(R.string.intro3),
                R.raw.intro_3
            ),
            ScreenItem(
                getString(R.string.intro4Title),
                getString(R.string.intro4),
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