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
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

class CountBillActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var deleteSoundPlayer: MediaPlayer
    private lateinit var billsAdapter: BillsAdapter
    private lateinit var db: SQLiteHelper

    private var tapCount = 0
    private val tapTimeout = 500L
    private val handler = Handler(Looper.getMainLooper())

    private var x1: Float = 0f
    private val swipeThreshold = 500

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
            borrarHistorial()
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

    private fun initializeComponents() {
        mediaPlayer = MediaPlayer.create(this, R.raw.historial_billetes)
        deleteSoundPlayer = MediaPlayer.create(this, R.raw.borrar_historial)
        mediaPlayer.start()

        db = SQLiteHelper(this)
    }

    private fun borrarHistorial() {
        db.deleteAllBills()
        updateUI()
        deleteSoundPlayer.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val bills = db.getAllBills().reversed()
        billsAdapter.updateBills(bills)
        findViewById<TextView>(R.id.totalTextView).text =
            "${getString(R.string.totalBilletes)}${db.getTotalAmount()}"
    }

    private fun handleTap() {
        tapCount++
        handler.removeCallbacksAndMessages(null)

        if (tapCount >= 5) {
            borrarHistorial()
            tapCount = 0
        } else {
            handler.postDelayed({ tapCount = 0 }, tapTimeout)
        }
    }

    private fun shareHistory() {
        val bills = db.getAllBills().reversed()
        val totalAmount = db.getTotalAmount()
        val file = saveHistoryToFile(bills, totalAmount)
        if (file != null) shareHistoryFile(file)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        deleteSoundPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

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
}