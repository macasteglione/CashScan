package com.example.reconocimiento_billetes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class CountBillActivity : ComponentActivity() {
    private val bills = listOf(
        Bill("1000", "01/10/2024"),
        Bill("500", "02/10/2024"),
        Bill("200", "03/10/2024"),
        Bill("100", "04/10/2024")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            app()
        }
    }
}

@Preview(showBackground = true)
@Composable

fun app() {
     val bills = listOf(
        Bill("1000", "01/10/2024"),
        Bill("500", "02/10/2024"),
        Bill("200", "03/10/2024"),
        Bill("100", "04/10/2024")
    )
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth()) {
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

        // List of bills
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bills) { bill ->
                Row(modifier = Modifier.fillMaxWidth()) {
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
            text = "Total: ${bills.sumOf { it.name.toInt() }}",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

