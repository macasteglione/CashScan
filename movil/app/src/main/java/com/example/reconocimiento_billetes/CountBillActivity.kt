package com.example.reconocimiento_billetes

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.data.SQLiteHelper
import com.example.reconocimiento_billetes.domain.BillData
import com.example.reconocimiento_billetes.presentation.BillsAdapter
import com.example.reconocimiento_billetes.presentation.getLocalizedAudioResId
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

/**
 * Actividad que permite gestionar el historial de billetes contados.
 * Muestra la lista de billetes contados, permite eliminar el historial,
 * compartir el historial y manejar interacciones como los toques rápidos para borrar el historial.
 */
class CountBillActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var deleteSoundPlayer: MediaPlayer
    private lateinit var billsAdapter: BillsAdapter
    private lateinit var db: SQLiteHelper

    private var tapCount = 0
    private val tapTimeout = 500L
    private val handler = Handler(Looper.getMainLooper())

    private var x1: Float = 0f
    private val swipeThreshold = 300

    /**
     * Método que se ejecuta al crear la actividad. Inicializa los componentes,
     * configura el RecyclerView, los botones y los eventos de toque.
     */
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_bill)

        initializeComponents()

        val recyclerView = findViewById<RecyclerView>(R.id.billsRecyclerView)
        billsAdapter = BillsAdapter(emptyList())
        recyclerView.apply {
            adapter = billsAdapter
            layoutManager = LinearLayoutManager(this@CountBillActivity)
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            deleteHistory()
        }

        findViewById<Button>(R.id.shareButton).setOnClickListener {
            shareHistory()
        }

        val touchListener = View.OnTouchListener { v, event ->
            v.performClick()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    handleTap()
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val x2 = event.x
                    val xDiff = x2 - x1

                    if (abs(xDiff) > swipeThreshold) {
                        if (xDiff > 0)
                            shareHistory()
                        else finish()
                    }
                    true
                }

                else -> false
            }
        }

        findViewById<RecyclerView>(R.id.billsRecyclerView).setOnTouchListener(touchListener)

        val bills = db.getAllBills().reversed()
        billsAdapter.updateBills(bills)
        findViewById<TextView>(R.id.totalTextView).text =
            "${getString(R.string.totalBilletes)}${db.getTotalAmount()}"
    }

    /**
     * Inicializa los componentes como los reproductores de audio y la base de datos.
     */
    private fun initializeComponents() {
        mediaPlayer = MediaPlayer.create(this, getLocalizedAudioResId(this, "historial_billetes"))
        deleteSoundPlayer =
            MediaPlayer.create(this, getLocalizedAudioResId(this, "borrar_historial"))
        mediaPlayer.start()

        db = SQLiteHelper(this)
    }

    /**
     * Elimina todos los billetes del historial de la base de datos
     * y actualiza la interfaz de usuario.
     */
    private fun deleteHistory() {
        db.deleteAllBills()
        updateUI()
        deleteSoundPlayer.start()
    }

    /**
     * Actualiza la interfaz de usuario con los billetes más recientes
     * y muestra el total de dinero.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val bills = db.getAllBills().reversed()
        billsAdapter.updateBills(bills)
        findViewById<TextView>(R.id.totalTextView).text =
            "${getString(R.string.totalBilletes)}${db.getTotalAmount()}"
    }

    /**
     * Maneja el conteo de toques rápidos. Si se tocan más de 4 veces en un corto periodo,
     * elimina el historial.
     */
    private fun handleTap() {
        tapCount++
        handler.removeCallbacksAndMessages(null)

        if (tapCount >= 5) {
            deleteHistory()
            tapCount = 0
        } else handler.postDelayed({ tapCount = 0 }, tapTimeout)
    }

    /**
     * Comparte el historial de billetes mediante un archivo.
     * Guarda el historial en un archivo y lo comparte mediante una intención.
     */
    private fun shareHistory() {
        val bills = db.getAllBills().reversed()
        val totalAmount = db.getTotalAmount()
        val file = saveHistoryToFile(bills, totalAmount)
        if (file != null) shareHistoryFile(file)
    }

    /**
     * Guarda el historial de billetes en un archivo de texto en el almacenamiento interno.
     */
    private fun saveHistoryToFile(
        bills: List<BillData>,
        totalAmount: Int
    ): File? {
        val fileName = "cashScan.txt"
        val file = File(this.filesDir, fileName)

        try {
            FileOutputStream(file).use { output ->
                output.write("${getString(R.string.totalBilletesArchivo)}$totalAmount\n\n".toByteArray())
                output.write("${getString(R.string.historialDeBilletes)}:\n".toByteArray())

                for (bill in bills) {
                    val line =
                        "${getString(R.string.valorBillete)}${bill.value}, ${getString(R.string.fechaBillete)}${bill.date}\n"
                    output.write(line.toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return file
    }

    /**
     * Comparte el archivo de historial utilizando un intent.
     */
    private fun shareHistoryFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${this.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.compartirHistorial)))
    }

    /**
     * Libera los recursos utilizados por los reproductores de audio y el handler.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        deleteSoundPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}