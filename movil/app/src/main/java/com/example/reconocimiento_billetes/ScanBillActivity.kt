package com.example.reconocimiento_billetes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

class ScanBillActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!hasCameraPermission())
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        setContent {
            ReconocimientobilletesTheme {
                App()
            }
        }
    }


    @Composable
    private fun App() {
        //var showCamera by remember { mutableStateOf(false) }
        var classifications by remember { mutableStateOf(emptyList<Classification>()) }
        var lastDetectedBill by remember { mutableIntStateOf(-1) }

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

        //sujeto a cambios
        DisposableEffect(Unit) {
            onDispose {
                cameraController.unbind()
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
        /*
        val latestClassifications by rememberUpdatedState(newValue = classifications)
        LaunchedEffect(latestClassifications) {
            //no descomentar ni borrar
        }*/

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
            CameraPreview(cameraController, Modifier.fillMaxSize())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.TopCenter)
            ) {
                classifications.forEach {
                    val labels = loadLabels(applicationContext)
                    val label = if (it.index < labels.size) labels[it.index] else "Desconocido"

                    // Solo reproducir audio si el billete detectado es diferente del anterior
                    if (it.index != lastDetectedBill) {
                        lastDetectedBill = it.index // Actualizar el último billete detectado
                        reproducirAudio(it.index)
                    }

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
        }
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

    private fun reproducirAudio(billete: Int) {
        val audioResId = when (billete) {
            0 -> R.raw.answer_10
            1 -> R.raw.answer_20
            2 -> R.raw.answer_50
            3 -> R.raw.answer_100
            4 -> R.raw.answer_200
            5 -> R.raw.answer_500
            6 -> R.raw.answer_1000
            7 -> R.raw.answer_2000
            8 -> R.raw.answer_10000
            else -> null
        }

        audioResId?.let {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            }

            mediaPlayer = MediaPlayer.create(this, it)
            mediaPlayer?.let { mp ->
                mp.start()

                mp.setOnCompletionListener { player ->
                    player.release()
                    mediaPlayer = null  // Limpiar la referencia al MediaPlayer
                }

                mp.setOnErrorListener { player, what, extra ->
                    player.release()
                    mediaPlayer = null  // Limpiar la referencia al MediaPlayer
                    true // Manejar el error
                }
            }
        }
    }
}