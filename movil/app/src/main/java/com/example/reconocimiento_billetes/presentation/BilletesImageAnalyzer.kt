package com.example.reconocimiento_billetes.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.reconocimiento_billetes.domain.BilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification

class BilletesImageAnalyzer(
    private val classifier: BilletesClassifier,
    private val onResults: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private val bufferSize = 9
    private val imageBuffer = mutableListOf<Bitmap>()
    private var frameSkipCounter = 0
    private val frameInterval = 20

    // Variables para controlar el proceso
    //private val handler = Handler(Looper.getMainLooper())
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
                if (results.isNotEmpty()) {
                    onResults(results)
                }
                // Vaciar el buffer después de procesar
                imageBuffer.clear()
            }
        }
        frameSkipCounter++

        // Cerrar la imagen después de procesar
        image.close()
    }

    private fun processBufferedImages(rotationDegree: Int): List<Classification> {

        val allResults = mutableListOf<Classification>()

        imageBuffer.forEach { bitmap ->
            val results = classifier.classify(bitmap, rotationDegree)
            allResults.addAll(results)
        }

        // Contar cuántas veces aparece cada clasificación
        val classificationCount = allResults.groupingBy { it.name }.eachCount()

        // Filtrar resultados que aparecen al menos 5 veces
        val frequentResults = classificationCount.filter { it.value >= 6 }.keys

        // Devolver los resultados únicos con 5 o más ocurrencias
        val finalResults = allResults.filter { it.name in frequentResults }.distinctBy { it.name }

        // debug
        if (finalResults.isNotEmpty()) {
            finalResults.forEach { classification ->
                Log.d(
                    "BilletesDetection",
                    "Etiqueta: ${classification.name}, Confianza: ${classification.score}"
                )
            }
        } else {
            Log.d("BilletesDetection", "No se detectó ningún billete con suficiente frecuencia")
        }

        // Filtrado de resultados repetidos por nombre de clasificación
        return allResults.distinctBy { it.name }
    }

    /*
    private fun pauseAnalysis(duration: Long) {
        analysisActive = false
        handler.postDelayed({
            analysisActive = true  // Reactivar el análisis después de 10 segundos
        }, duration)
    }
    */
}