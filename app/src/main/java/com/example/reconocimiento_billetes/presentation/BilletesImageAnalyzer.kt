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
    private val frameInterval = 10

    override fun analyze(image: ImageProxy) {
        Log.d("Analyzer", "Processing frame")

        if (frameSkipCounter % frameInterval == 0) {
            val rotationDegree = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(256, 256)

            val results = classifier.classify(bitmap, rotationDegree)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}