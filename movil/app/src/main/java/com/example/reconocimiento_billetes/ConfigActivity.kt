package com.example.reconocimiento_billetes

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.presentation.ModelListAdapter
import com.example.reconocimiento_billetes.presentation.getLocalizedAudioResId

/**
 * ConfigActivity permite al usuario seleccionar un modelo desde una lista y escuchar un sonido
 * cuando realiza una selección. Además, gestiona la navegación entre pantallas con un botón
 * y el deslizamiento de la pantalla.
 */
class ConfigActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var modelListRecyclerView: RecyclerView
    private lateinit var selectModelButton: Button
    private lateinit var modelListAdapter: ModelListAdapter

    private val thresholdWidth get() = (resources.displayMetrics.widthPixels * 0.25f)
    private val modelNames: Array<String> by lazy { resources.getStringArray(R.array.model_names) }
    private val doubleTapThreshold: Long = 300

    private var selectedModelIndex: Int? = null
    private var startX = 0f
    private var lastTapTime: Long = 0

    /**
     * Método de inicialización de la actividad.
     * Configura los elementos de la interfaz y reproduce el sonido inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        mediaPlayer = MediaPlayer.create(this, getLocalizedAudioResId(this, "configuracion"))
        mediaPlayer.start()

        // Recupera el índice del modelo seleccionado
        selectedModelIndex = intent.getIntExtra("selectedModelIndex", -1)

        modelListRecyclerView = findViewById(R.id.model_list)
        selectModelButton = findViewById(R.id.select_model_button)

        // Configura el RecyclerView con un LinearLayoutManager
        modelListRecyclerView.layoutManager = LinearLayoutManager(this)

        modelListAdapter = ModelListAdapter(modelNames.toList()) { _, position ->
            selectedModelIndex = position
            playSelectionSound()
            modelListAdapter.setSelectedPosition(
                selectedModelIndex ?: 0
            )
        }

        modelListRecyclerView.adapter = modelListAdapter

        selectedModelIndex?.let { index ->
            if (index >= 0) modelListAdapter.setSelectedPosition(index)
        }

        selectModelButton.setOnClickListener {
            selectedModelIndex?.let { index ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("selectedModelIndex", index)
                startActivity(intent)
                finish()
            }
        }

        setupTouchListener()
    }

    /**
     * Libera el reproductor de medios al destruir la actividad.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    /**
     * Configura un listener táctil para detectar deslizamientos y toques en la pantalla.
     * Permite navegar entre elementos o finalizar la actividad mediante gestos.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        val touchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                MotionEvent.ACTION_UP -> {
                    val deltaX = event.x - startX

                    if (deltaX > thresholdWidth) selectNextItem()

                    if (deltaX < -thresholdWidth) finish()

                    if (isDoubleTap()) {
                        selectedModelIndex?.let { index ->
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("selectedModelIndex", index)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            true
        }

        findViewById<RecyclerView>(R.id.model_list).setOnTouchListener(touchListener)
    }

    /**
     * Cambia la selección al siguiente ítem de la lista.
     */
    private fun selectNextItem() {
        selectedModelIndex = (selectedModelIndex ?: -1) + 1
        selectedModelIndex = selectedModelIndex?.takeIf { it < modelListAdapter.itemCount } ?: 0
        modelListAdapter.setSelectedPosition(selectedModelIndex ?: 0)
        playSelectionSound()
    }

    /**
     * Detecta si ha ocurrido un doble toque en la pantalla.
     * @return true si se detecta un doble toque, false en caso contrario.
     */
    private fun isDoubleTap(): Boolean {
        val currentTime = System.currentTimeMillis()
        val isDoubleTap = currentTime - lastTapTime < doubleTapThreshold
        lastTapTime = currentTime
        return isDoubleTap
    }

    /**
     * Reproduce un sonido asociado al modelo seleccionado.
     * El sonido se selecciona dinámicamente según el nombre del modelo.
     */
    private fun playSelectionSound() {
        try {
            // Se crea un nuevo reproductor de medios con el sonido correspondiente al modelo
            mediaPlayer = MediaPlayer.create(
                this,
                getLocalizedAudioResId(
                    this,
                    "configuracion_${modelNames[selectedModelIndex!!].split(" ")[0].lowercase()}"
                )
            )
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}