package com.example.reconocimiento_billetes.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class LuminosityAnalyzer(val onLowLightDetected: (Boolean) -> Unit) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val pixels = ByteArray(buffer.capacity())
        buffer.get(pixels)

        val luminance = pixels.map { it.toInt() and 0xFF }.average()
        onLowLightDetected(luminance < 50)
    }
}