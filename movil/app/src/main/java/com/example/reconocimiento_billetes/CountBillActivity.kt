package com.example.reconocimiento_billetes

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import com.example.reconocimiento_billetes.data.SQLiteHelper
import com.example.reconocimiento_billetes.domain.BillData
import com.example.reconocimiento_billetes.ui.theme.CountBillActivityTheme
import java.io.File
import java.io.FileOutputStream

class CountBillActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var deleteSoundPlayer: MediaPlayer

    private var tapCount = 0
    private val tapTimeout = 500L
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaPlayer = MediaPlayer.create(this, R.raw.historial_billetes)
        deleteSoundPlayer = MediaPlayer.create(this, R.raw.borrar_historial)
        mediaPlayer.start()

        val db = SQLiteHelper(this)
        var bills = db.getAllBills().reversed()
        var totalAmount = db.getTotalAmount()

        setContent {
            CountBillActivityTheme(
                bills = bills,
                closeAct = { finish() },
                totalAmount = totalAmount,
                onClearHistory = {
                    db.deleteAllBills()
                    bills = db.getAllBills().reversed()
                    totalAmount = db.getTotalAmount()
                    deleteSoundPlayer.start()
                    mediaPlayer.release()
                    recreate()
                },
                onSaveAndShareHistory = {
                    val file = saveHistoryToFile(this, bills, totalAmount)
                    if (file != null) shareHistoryFile(this, file)
                },
                onScreenTap = { handleTap(db) }
            )
        }
    }

    private fun handleTap(db: SQLiteHelper) {
        tapCount++
        handler.removeCallbacksAndMessages(null)

        if (tapCount >= 5) {
            db.deleteAllBills()
            recreate()
            tapCount = 0
        } else handler.postDelayed({ tapCount = 0 }, tapTimeout)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        deleteSoundPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    private fun saveHistoryToFile(
        context: Context,
        bills: List<BillData>,
        totalAmount: Int
    ): File? {
        val fileName = "historial_billetes.txt"
        val file = File(context.filesDir, fileName)

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

    private fun shareHistoryFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Compartir historial de billetes"))
    }
}