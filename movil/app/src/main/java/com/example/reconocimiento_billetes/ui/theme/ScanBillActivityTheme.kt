package com.example.reconocimiento_billetes.ui.theme

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.reconocimiento_billetes.domain.Classification
import com.example.reconocimiento_billetes.presentation.CameraPreview

@Composable
fun ScanBillActivityTheme(
    classification: Classification?,
    cameraController: LifecycleCameraController,
    reproducirAudio: (Int) -> Unit,
    vibrateDevice: () -> Unit,
    guardarBaseDeDatos: (Int) -> Unit,
    getLabelFromIndex: (Int) -> String,
    onFinish: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val percentage = 0.37f
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
                    if (offsetX < -thresholdWidth) onFinish()
                }
            }
    ) {
        CameraPreview(cameraController, Modifier.fillMaxSize())

        if (classification == null) {
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
            classification.let { result ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .align(Alignment.TopCenter)
                ) {
                    reproducirAudio(result.index)

                    //invertir flujo pendiente
                    vibrateDevice()
                    guardarBaseDeDatos(getLabelFromIndex(result.index).toInt())

                    Text(
                        text = "Billete de $${getLabelFromIndex(result.index)}",
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