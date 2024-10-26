package com.example.reconocimiento_billetes.presentation

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CombinedImageAnalyzer(
    private val billetesAnalyzer: BilletesImageAnalyzer,
    private val luminosityAnalyzer: LuminosityAnalyzer
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        try {
            luminosityAnalyzer.analyze(image)
            billetesAnalyzer.analyze(image)
        }catch(ex: Exception){
            Log.d("Scanner", "hubo un error trabajando las imagenes")
        } finally {
            image.close()
        }
    }
}