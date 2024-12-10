package com.example.reconocimiento_billetes

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.reconocimiento_billetes.presentation.getLocalizedAudioResId

class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    private var startX = 0f
    private var startY = 0f
    private val thresholdWidth get() = (resources.displayMetrics.widthPixels * 0.25f)
    private val thresholdHeight get() = (resources.displayMetrics.heightPixels * 0.25f)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectedModelIndex = intent.getIntExtra("selectedModelIndex", 0)
        val modelNames = resources.getStringArray(R.array.model_names)

        window.statusBarColor = ContextCompat.getColor(this, R.color.green)

        mediaPlayer = MediaPlayer.create(this, getLocalizedAudioResId(this, "menu_principal"))

        findViewById<CardView>(R.id.ScanButton).setOnClickListener {
            val intent = Intent(this, ScanBillActivity::class.java)
            intent.putExtra(
                "selectedModel",
                modelNames[selectedModelIndex].split(" ")[0].lowercase()
            )
            startActivity(intent)
        }

        findViewById<CardView>(R.id.HistoryButton).setOnClickListener {
            startActivity(Intent(this, CountBillActivity::class.java))
        }

        findViewById<CardView>(R.id.TutorialButton).setOnClickListener {
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        findViewById<CardView>(R.id.FeedbackButton).setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            intent.putExtra("selectedModelIndex", selectedModelIndex)
            startActivity(intent)
        }

        val touchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }

                MotionEvent.ACTION_UP -> {
                    val deltaX = event.x - startX
                    val deltaY = event.y - startY

                    if (deltaX > thresholdWidth)
                        startActivity(Intent(this, ScanBillActivity::class.java))
                    else if (deltaX < -thresholdWidth)
                        startActivity(Intent(this, CountBillActivity::class.java))

                    if (deltaY > thresholdHeight) {
                        startActivity(Intent(this, ConfigActivity::class.java))
                    } else if (deltaY < -thresholdHeight)
                        startActivity(Intent(this, TutorialActivity::class.java))
                }
            }
            true
        }

        findViewById<RelativeLayout>(R.id.main_layout).setOnTouchListener(touchListener)
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}