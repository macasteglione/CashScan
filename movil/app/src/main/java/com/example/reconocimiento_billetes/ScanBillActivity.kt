package com.example.reconocimiento_billetes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reconocimiento_billetes.data.SQLiteHelper
import com.example.reconocimiento_billetes.data.TfLiteBilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification
import com.example.reconocimiento_billetes.presentation.BilletesImageAnalyzer
import com.example.reconocimiento_billetes.presentation.CameraPreview
import com.example.reconocimiento_billetes.presentation.CombinedImageAnalyzer
import com.example.reconocimiento_billetes.presentation.LuminosityAnalyzer
import com.example.reconocimiento_billetes.presentation.getCurrentDateTime
import com.example.reconocimiento_billetes.ui.theme.ReconocimientobilletesTheme


private var mediaPlayer: MediaPlayer? = null
private var canVibrate = false
class ScanBillActivity : ComponentActivity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasCameraPermission())
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        canVibrate = vibrator.hasVibrator()

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
                                    finish()
                                }
                            }
                        }
                ){
                    App()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
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
                if (mp.isPlaying) mp.stop()
                mp.release()
            }

            mediaPlayer = MediaPlayer.create(this, it)
            mediaPlayer?.let { mp ->
                mp.start()

                mp.setOnCompletionListener { player ->
                    player.release()
                    mediaPlayer = null
                }

                mp.setOnErrorListener { player, _, _ ->
                    player.release()
                    mediaPlayer = null
                    true
                }
            }
        }
    }

    private fun playLowLightSound() {
        if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(this, R.raw.campana)

        // Reproducir el sonido si no se está reproduciendo actualmente
        mediaPlayer?.let {
            if (!it.isPlaying) it.start()
        }
    }

    private fun guardarBaseDeDatos(billete: Int) {
        val db = SQLiteHelper(this)
        db.insertBill(billete, getCurrentDateTime())
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun loadLabels(): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = this.assets.open("labels.txt")

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { labels.add(it) }
        }

        return labels
    }

    @Composable
    private fun App() {
        var classification by remember { mutableStateOf<Classification?>(null) }
        var ultimoBillete by remember { mutableIntStateOf(-1) }

        val billetesAnalyzer = remember {
            BilletesImageAnalyzer(
                classifier = TfLiteBilletesClassifier(
                    context = this
                ),
                onResult = { result ->
                    classification = result // Puede ser null si no hay resultados
                }
            )
        }

        val cameraController = remember {
            LifecycleCameraController(this).apply {
                setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                cameraController.unbind()
            }
        }

        val lightAnalyzer = remember {
            LuminosityAnalyzer { isLowLight ->
                if (isLowLight) {
                    cameraController.enableTorch(true)
                    playLowLightSound()
                }
                else cameraController.enableTorch(false)
            }
        }

        val combinedAnalyzer = remember {
            CombinedImageAnalyzer(billetesAnalyzer, lightAnalyzer)
        }

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            combinedAnalyzer
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CameraPreview(cameraController, Modifier.fillMaxSize())

            // Verificar si classification es null
            if (classification == null) {
                // Mostrar un mensaje indicando que no se detectó nada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "No se ha detectado ningún billete.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 40.dp, 0.dp, 16.dp),
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                // Si classification no es null, mostrar el billete detectado
                classification?.let { result ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .align(Alignment.TopCenter)
                    ) {
                        val labels = loadLabels()
                        val label = if (result.index < labels.size) labels[result.index] else "Desconocido"

                        if (result.index != ultimoBillete) {
                            ultimoBillete = result.index
                            reproducirAudio(result.index)
                            vibrateDevice()
                            guardarBaseDeDatos(Integer.parseInt(label))
                        }

                        Text(
                            text = "Billete de $$label",
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
    }

    private fun vibrateDevice(duration: Long = 300L) {
        if (canVibrate) {
            vibrator.vibrate(duration)
        }
    }
}