package com.example.reconocimiento_billetes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.reconocimiento_billetes.data.TfLiteBilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification
import com.example.reconocimiento_billetes.presentation.BilletesImageAnalyzer
import com.example.reconocimiento_billetes.presentation.CameraPreview
import com.example.reconocimiento_billetes.presentation.CombinedImageAnalyzer
import com.example.reconocimiento_billetes.presentation.LuminosityAnalyzer
import com.example.reconocimiento_billetes.ui.theme.ReconocimientobilletesTheme

class ScanBillActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReconocimientobilletesTheme {
            App()
        }
    }
}

    @Composable
    private fun App() {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
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
}