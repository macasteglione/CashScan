package com.example.reconocimiento_billetes

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.reconocimiento_billetes.presentation.getCurrentDateTime
import com.example.reconocimiento_billetes.ui.theme.ReconocimientobilletesTheme

class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaPlayer = MediaPlayer.create(this, R.raw.menu_principal)

        setContent {
            ReconocimientobilletesTheme {
                var offsetX by remember { mutableFloatStateOf(0f) }

                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val percentage = 0.45f
                val thresholdWidth = with(LocalDensity.current) { screenWidth.toPx() * percentage }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (offsetX in -thresholdWidth..thresholdWidth) {
                                        offsetX = 0f
                                    }
                                },
                                onDragCancel = {
                                    offsetX = 0f
                                }
                            ) { _, dragAmount ->
                                offsetX += dragAmount.x

                                when {
                                    offsetX < -thresholdWidth -> {
                                        offsetX = 0f
                                        val intent = Intent(this@MainActivity, CountBillActivity::class.java)
                                        startActivity(intent)
                                    }
                                    offsetX > thresholdWidth -> {
                                        offsetX = 0f
                                        val intent = Intent(this@MainActivity, ScanBillActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                ) {
                    FondoImagen()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)
                            .align(Alignment.Center)
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

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    @Preview(showBackground = true)
    @Composable
    private fun FondoImagen() {
        Image(
            painter = painterResource(id = R.drawable.logo2), // Coloca aquí tu imagen
            contentDescription = null,
            contentScale = ContentScale.Fit, // Ajustar la imagen manteniendo su proporción
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        )
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
                    if (!mp.isPlaying) mp.start()
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