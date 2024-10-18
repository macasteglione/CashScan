package com.example.reconocimiento_billetes

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reconocimiento_billetes.presentation.getCurrentDateTime
import com.example.reconocimiento_billetes.ui.theme.ReconocimientobilletesTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReconocimientobilletesTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BotonCountBill()
                        BotonDate()
                        BotonPlaySound(this@MainActivity)
                        BotonScanBill()
                    }
                }
            }
        }
    }

    @Composable
    private fun BotonScanBill() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, ScanBillActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Escanear Billete")
            }
        }
    }

    @Composable
    private fun BotonPlaySound(context: Context) {
        val mp: MediaPlayer = remember {
            MediaPlayer.create(context, R.raw.campana)
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (!mp.isPlaying) {
                        mp.start()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Reproducir Sonido")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                mp.release()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun BotonCountBill() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, CountBillActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Ir a Historial de Billetes")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun BotonDate() {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val currentDateTime = getCurrentDateTime()
                    Toast.makeText(
                        context,
                        "Fecha y hora actual:  \n $currentDateTime",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Fecha y hora actual")
            }
        }
    }
}