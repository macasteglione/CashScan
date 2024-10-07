package com.example.reconocimiento_billetes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reconocimiento_billetes.data.TfLiteBilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification
import com.example.reconocimiento_billetes.presentation.BilletesImageAnalyzer
import com.example.reconocimiento_billetes.presentation.CameraPreview
import com.example.reconocimiento_billetes.presentation.CombinedImageAnalyzer
import com.example.reconocimiento_billetes.presentation.LuminosityAnalyzer
import com.example.reconocimiento_billetes.ui.theme.ReconocimientobilletesTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasCameraPermission())
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        setContent {
            ReconocimientobilletesTheme {
                var showCamera by remember { mutableStateOf(false) }
                var classifications by remember { mutableStateOf(emptyList<Classification>()) }

                val billetesAnalyzer = remember {
                    BilletesImageAnalyzer(
                        classifier = TfLiteBilletesClassifier(
                            context = applicationContext
                        ),
                        onResults = {
                            classifications = it
                        }
                    )
                }

                val cameraController = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    }
                }

                val lightAnalyzer = remember {
                    LuminosityAnalyzer { isLowLight ->
                        if (isLowLight) {
                            cameraController.enableTorch(true)
                        } else {
                            cameraController.enableTorch(false)
                        }
                    }
                }

                val combinedAnalyzer = remember {
                    CombinedImageAnalyzer(billetesAnalyzer, lightAnalyzer)
                }

                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(applicationContext),
                    combinedAnalyzer
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (showCamera) {
                        CameraPreview(cameraController, Modifier.fillMaxSize())
                    } else {
                        Button(
                            onClick = { showCamera = true },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(text = "Abrir Cámara")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .align(Alignment.TopCenter)
                    ) {
                        classifications.forEach {
                            val labels = loadLabels(applicationContext)
                            val label =
                                if (it.index < labels.size) labels[it.index] else "Desconocido"

                            Text(
                                text = label,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 40.dp, 0.dp, 16.dp),
                                fontSize = 20.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        }

                    }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BotonCountBill()
                        BotonDate()
                        PlaySound(this@MainActivity)
                    }


                }
            }

        }
    }

    //@Preview(showBackground = true)
    @Composable
    private fun PlaySound(context: Context) {
        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.campana)
        /*
        detener mediaPlayer en caso de cerrar la actividad(sólo utilizar en actividades fuera del main)
        mediaPlayer?.release()
        mediaPlayer = null
         */
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    mp.start()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Reproducir Sonido")
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

    private fun getCurrentDateTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentDateTime)
    }


    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun loadLabels(context: Context): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = context.assets.open("labels.txt")
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { labels.add(it) }
        }
        return labels
    }

}