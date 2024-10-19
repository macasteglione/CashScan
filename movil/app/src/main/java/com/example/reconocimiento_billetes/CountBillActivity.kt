package com.example.reconocimiento_billetes

import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reconocimiento_billetes.data.SQLiteHelper
import com.example.reconocimiento_billetes.domain.BillData

class CountBillActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = SQLiteHelper(this)
        var bills = db.getAllBills()
        var totalAmount = db.getTotalAmount()

        setContent {
            App(
                bills = bills,
                closeAct = { finish() },
                totalAmount = totalAmount,
                onClearHistory = {
                    db.deleteAllBills()
                    bills = db.getAllBills()
                    totalAmount = db.getTotalAmount()
                }
            )
        }
    }
}

@Composable
fun App(bills: List<BillData>, totalAmount: Int, onClearHistory: () -> Unit, closeAct: () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        closeAct()
                    }
                )
                detectDragGestures { _, dragAmount ->
                    offsetX += dragAmount.x
                    if (offsetX < -50f) {
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
    }
}