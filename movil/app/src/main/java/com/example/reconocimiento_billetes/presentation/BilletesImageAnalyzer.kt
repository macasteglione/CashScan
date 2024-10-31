package com.example.reconocimiento_billetes.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.reconocimiento_billetes.domain.BilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification

class BilletesImageAnalyzer(
    private val classifier: BilletesClassifier,
    private val onResult: (Classification?) -> Unit // Cambiar de List a un único Classification
) : ImageAnalysis.Analyzer {

    private val bufferSize = 10
    private val imageBuffer = mutableListOf<Bitmap>()
    private var frameSkipCounter = 0
    private val frameInterval = 15

    private var analysisActive = true

    override fun analyze(image: ImageProxy) {
        if (!analysisActive) {
            image.close()
            return
        }

        if (frameSkipCounter % frameInterval == 0) {
            val rotationDegree = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap().centerCrop(224, 224)
            // Agregar imagen al buffer
            imageBuffer.add(bitmap)

            // Si el buffer está lleno, procesar imágenes en lote
            if (imageBuffer.size >= bufferSize) {
                val result = processBufferedImages(rotationDegree)
                // Pasar solo un resultado, o null si no hay
                onResult(result)
                clearBuffer()
            }
        }
        frameSkipCounter++
        image.close()
    }

    private fun processBufferedImages(rotationDegree: Int): Classification? {
        val allResults = mutableListOf<Classification>()

        imageBuffer.forEach { bitmap ->
            val results = classifier.classify(bitmap, rotationDegree)
            allResults.addAll(results)
        }

        // Contar cuántas veces aparece cada clasificación
        val classificationCount = allResults.groupingBy { it.name }.eachCount()

        // Filtrar el resultado que aparece más veces (mayor frecuencia)
        val mostFrequentResult = classificationCount.maxByOrNull { it.value }?.key

        // Si encontramos un resultado frecuente, devolverlo
        val finalResult = allResults.firstOrNull { it.name == mostFrequentResult }

        // Log para debug
        if (finalResult != null) {
            Log.d(
                "BilletesDetection",
                "Etiqueta: ${finalResult.name}, Confianza: ${finalResult.score}"
            )
        } else {
            Log.d("BilletesDetection", "No se detectó ningún billete con suficiente frecuencia")
        }

        // Devolver un único resultado, o null si no hay resultados
        return finalResult
    }

    private fun pauseAnalysis() {
        analysisActive = false
    }

    // Método público para reactivar el análisis desde la UI
    fun resumeAnalysis() {
        analysisActive = true
    }

    private fun clearBuffer() {
        imageBuffer.clear()
    }
}
