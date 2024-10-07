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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
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
        /*Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {*/
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
            }
        //}

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