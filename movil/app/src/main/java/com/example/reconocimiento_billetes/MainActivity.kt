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

/**
 * Actividad principal que muestra el menú con opciones para acceder a diferentes funciones.
 * Incluye navegación mediante botones y deslizamientos táctiles.
 */
class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    private var startX = 0f
    private var startY = 0f

    private val thresholdWidth get() = (resources.displayMetrics.widthPixels * 0.25f)
    private val thresholdHeight get() = (resources.displayMetrics.heightPixels * 0.25f)

    /**
     * Se ejecuta cuando se crea la actividad. Inicializa los componentes de la UI y maneja
     * la navegación y los gestos táctiles.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtiene el índice del modelo seleccionado y los nombres de los modelos
        val selectedModelIndex = intent.getIntExtra("selectedModelIndex", 0)
        val modelNames = resources.getStringArray(R.array.model_names)

        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
        mediaPlayer = MediaPlayer.create(this, getLocalizedAudioResId(this, "menu_principal"))

        setupButtons(modelNames, selectedModelIndex)
        setupTouchListener(selectedModelIndex)
    }

    /**
     * Configura los botones del menú principal con sus respectivas acciones.
     */
    private fun setupButtons(modelNames: Array<String>, selectedModelIndex: Int) {
        findViewById<CardView>(R.id.ScanButton).setOnClickListener {
            val intent = Intent(this, ScanBillActivity::class.java)
            // Envía el modelo seleccionado a la actividad ScanBillActivity
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

        findViewById<CardView>(R.id.ConfigButton).setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            intent.putExtra("selectedModelIndex", selectedModelIndex)
            startActivity(intent)
        }
    }

    /**
     * Configura el escuchador de deslizamientos táctiles en la pantalla principal.
     * Detecta los deslizamientos en las direcciones horizontal y vertical para navegar.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener(selectedModelIndex: Int) {
        val touchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }

                MotionEvent.ACTION_UP -> {
                    // Calcula las diferencias en las coordenadas para detectar el deslizamiento
                    val deltaX = event.x - startX
                    val deltaY = event.y - startY

                    if (deltaX > thresholdWidth)
                        startActivity(Intent(this, ScanBillActivity::class.java))
                    else if (deltaX < -thresholdWidth)
                        startActivity(Intent(this, CountBillActivity::class.java))

                    if (deltaY > thresholdHeight) {
                        val intent = Intent(this, ConfigActivity::class.java)
                        intent.putExtra("selectedModelIndex", selectedModelIndex)
                        startActivity(intent)
                    } else if (deltaY < -thresholdHeight)
                        startActivity(Intent(this, TutorialActivity::class.java))
                }
            }
            true
        }

        findViewById<RelativeLayout>(R.id.main_layout).setOnTouchListener(touchListener)
    }

    /**
     * Se ejecuta cuando la actividad se pone en primer plano. Reproduce el audio.
     */
    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    /**
     * Se ejecuta cuando la actividad entra en segundo plano. Pausa el audio si está en reproducción.
     */
    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    /**
     * Se ejecuta cuando la actividad se destruye. Libera el reproductor de medios.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
