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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.reconocimiento_billetes.data.SQLiteHelper
import com.example.reconocimiento_billetes.domain.BillData
import java.io.File
import java.io.FileOutputStream

class CountBillActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var deleteSoundPlayer: MediaPlayer

    private var tapCount = 0
    private val tapTimeout = 500L // Tiempo en ms para reiniciar el conteo de toques
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
            App(
                bills = bills,
                closeAct = { finish() },
                totalAmount = totalAmount,
                onClearHistory = {
                    db.deleteAllBills()
                    bills = db.getAllBills().reversed()
                    totalAmount = db.getTotalAmount()
                    mediaPlayer.release()
                    deleteSoundPlayer.start()
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

@Composable
fun App(
    bills: List<BillData>,
    totalAmount: Int,
    onClearHistory: () -> Unit,
    closeAct: () -> Unit,
    onSaveAndShareHistory: () -> Unit,
    onScreenTap: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    // Obtener el ancho de la pantalla
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val percentage = 0.45f // Ajustar a 45%
    val thresholdWidth = with(LocalDensity.current) { screenWidth.toPx() * percentage }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onScreenTap() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX > -thresholdWidth) {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX += dragAmount.x
                        if (offsetX < -thresholdWidth) {
                            closeAct()
                        }
                    }
                }
        ) {
            Text(
                text = "Historial de Billetes",
                fontSize = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Billete",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Fecha",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(bills) { bill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "$" + bill.value,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = bill.date,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Text(
                text = "Total: $$totalAmount",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = { onClearHistory() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Borrar Historial")
            }

            Button(
                onClick = { onSaveAndShareHistory() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Guardar y Compartir Historial")
            }
        }
    }
}