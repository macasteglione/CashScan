package com.example.reconocimiento_billetes.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CombinedImageAnalyzer(
    private val billetesAnalyzer: BilletesImageAnalyzer,
    private val luminosityAnalyzer: LuminosityAnalyzer
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        luminosityAnalyzer.analyze(image)
        billetesAnalyzer.analyze(image)

        image.close()
    }
}