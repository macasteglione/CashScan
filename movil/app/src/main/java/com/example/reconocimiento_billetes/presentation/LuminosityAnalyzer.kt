package com.example.reconocimiento_billetes.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.os.SystemClock

class LuminosityAnalyzer(
    val onLowLightDetected: (Boolean) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastTorchToggleTime: Long = 0
    private val torchToggleThreshold: Long = 9000

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val pixels = ByteArray(buffer.capacity())
        buffer.get(pixels)

        val luminance = pixels.map { it.toInt() and 0xFF }.average()

        // Obtener el tiempo actual
        val currentTime = SystemClock.elapsedRealtime()

        // Verificar si han pasado al menos 2 segundos desde el último cambio de linterna
        if (currentTime - lastTorchToggleTime >= torchToggleThreshold) {
            onLowLightDetected(luminance < 25)
            lastTorchToggleTime = currentTime
        }
    }
}
