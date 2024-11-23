package com.example.reconocimiento_billetes

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
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
    private lateinit var gestureDetector: GestureDetector

    private var tapCount = 0
    private val tapTimeout = 500L
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_bill)

        initGestureDetector()
        initializeComponents()

        val recyclerView = findViewById<RecyclerView>(R.id.billsRecyclerView)
        billsAdapter = BillsAdapter(emptyList())
        recyclerView.apply {
            adapter = billsAdapter
            layoutManager = LinearLayoutManager(this@CountBillActivity)
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            db.deleteAllBills()
            updateUI()
            deleteSoundPlayer.start()
        }

        findViewById<Button>(R.id.shareButton).setOnClickListener {
            shareHistory()
        }

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> handleTap()
            }
            gestureDetector.onTouchEvent(event)
            true
        }

        val bills = db.getAllBills().reversed()
        billsAdapter.updateBills(bills)
        findViewById<TextView>(R.id.totalTextView).text = "Total: $${db.getTotalAmount()}"
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0)
                            shareHistory()
                        else
                            finish()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun initializeComponents() {
        mediaPlayer = MediaPlayer.create(this, R.raw.historial_billetes)
        deleteSoundPlayer = MediaPlayer.create(this, R.raw.borrar_historial)
        mediaPlayer.start()

        db = SQLiteHelper(this)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val bills = db.getAllBills().reversed()
        billsAdapter.updateBills(bills)
        findViewById<TextView>(R.id.totalTextView).text = "Total: $${db.getTotalAmount()}"
    }

    private fun handleTap() {
        tapCount++
        handler.removeCallbacksAndMessages(null)

        if (tapCount >= 5) {
            db.deleteAllBills()
            updateUI()
            tapCount = 0
        } else {
            handler.postDelayed({ tapCount = 0 }, tapTimeout)
        }
    }

    private fun shareHistory() {
        val bills = db.getAllBills().reversed()
        val totalAmount = db.getTotalAmount()
        val file = saveHistoryToFile(bills, totalAmount)
        if (file != null)
            shareHistoryFile(file)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        deleteSoundPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private fun saveHistoryToFile(
        bills: List<BillData>,
        totalAmount: Int
    ): File? {
        val fileName = "historial_billetes.txt"
        val file = File(this.filesDir, fileName)

        try {
            FileOutputStream(file).use { output ->
                output.write("Total de Billetes: $$totalAmount\n\n".toByteArray())
                output.write("Historial de Billetes:\n".toByteArray())

                for (bill in bills) {
                    val line = "Billete: $${bill.value}, Fecha: ${bill.date}\n"
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

        startActivity(Intent.createChooser(intent, "Compartir historial de billetes"))
    }
}