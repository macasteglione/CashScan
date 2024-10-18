package com.example.reconocimiento_billetes.presentation

import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.reconocimiento_billetes.domain.BilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification

class BilletesImageAnalyzer(
    private val classifier: BilletesClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private val bufferSize = 8
    private val imageBuffer = mutableListOf<Bitmap>()
    private var frameSkipCounter = 0
    private val frameInterval = 20

    // Variables para controlar el proceso
    private val handler = Handler()
    private var analysisActive = true

    override fun analyze(image: ImageProxy) {
        if (!analysisActive) {
            // Si el análisis no está activo, se cierra la imagen y se retorna
            image.close()
            return
        }


        if (frameSkipCounter % frameInterval == 0) {
            val rotationDegree = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap().centerCrop(256, 256)

            // Agregar imagen al buffer
            imageBuffer.add(bitmap)

            // Si el buffer está lleno, procesar imágenes en lote
            if (imageBuffer.size >= bufferSize) {
                val results = processBufferedImages(rotationDegree)
                onResults(results)

                // Vaciar el buffer después de procesar
                imageBuffer.clear()
            }
        }
        frameSkipCounter++

        // Cerrar la imagen después de procesar
        image.close()
    }

    private fun processBufferedImages(rotationDegree: Int): List<Classification> {

        // Acumulamos los los resultados de cada imagen en el buffer
        val allResults = mutableListOf<Classification>()

        imageBuffer.forEach { bitmap ->
            val results = classifier.classify(bitmap, rotationDegree)
            allResults.addAll(results)
        }
        // Filtrar resultados repetidos por nombre de clasificación
        val uniqueResults = allResults.distinctBy { it.name }

        // debug
        if (uniqueResults.isNotEmpty()) {
            uniqueResults.forEach { classification ->
                Log.d("BilletesDetection", "Etiqueta: ${classification.name}, Confianza: ${classification.score}")
            }
        } else {
            Log.d("BilletesDetection", "No se detectó ningún billete")
        }

        // Filtrado de resultados repetidos por nombre de clasificación
        return allResults.distinctBy { it.name }
    }
}