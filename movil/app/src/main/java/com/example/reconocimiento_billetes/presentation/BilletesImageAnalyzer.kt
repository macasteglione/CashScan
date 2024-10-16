package com.example.reconocimiento_billetes.presentation

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.reconocimiento_billetes.domain.BilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification

class BilletesImageAnalyzer (
    private val classifier: BilletesClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0
    private val frameInterval = 20

    override fun analyze(image: ImageProxy) {
        Log.d("BilletesAnalyzer", "Processing frame number: $frameSkipCounter")

        if (frameSkipCounter % frameInterval == 0) {
            val rotationDegree = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(256, 256)

            val results = classifier.classify(bitmap, rotationDegree)

            results.forEach { classification ->
                Log.d("BilletesAnalyzer", "Resultado: Etiqueta: ${classification.name}, Confianza: ${classification.score}")
            }

            onResults(results)
        }

        frameSkipCounter++
    }
}