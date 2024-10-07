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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Bill(val name: String, val date: String)

class CountBillActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(closeAct = { finish() })
        }
    }
}

@Composable
fun App(closeAct: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    val bills = listOf(
        Bill("1000", "01/10/2024"),
        Bill("500", "02/10/2024"),
        Bill("200", "03/10/2024"),
        Bill("100", "04/10/2024")
    )
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
            }.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    offsetX += dragAmount.x}
                if (offsetX < -50f) {
                    closeAct()
                }
            }
    ) {

        // Texto del encabezado
        Text(
            text = "Historial de Billetes",
            fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Agrega un espacio debajo del título
            style = MaterialTheme.typography.titleLarge
        )

        // Header de la tabla
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
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

        // Lista de billetes
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bills) { bill ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) { // Espacio entre filas
                    Text(
                        text = bill.name,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = bill.date,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Total
        Text(
            text = "Total: $${bills.sumOf { it.name.toInt() }}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp) // Espacio superior para el total
        )
    }


}




