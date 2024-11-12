package com.example.reconocimiento_billetes.presentation

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CombinedImageAnalyzer(
    private val billetesAnalyzer: BilletesImageAnalyzer,
    private val luminosityAnalyzer: LuminosityAnalyzer
) : ImageAnalysis.Analyzer {


    override fun analyze(image: ImageProxy) {
        analyzeLuminosity(image)
        //clasifyBill(image)
    }


    fun analyzeLuminosity(image: ImageProxy){
        try {
            luminosityAnalyzer.analyze(image)
        }catch(ex: Exception){
            Log.d("Scanner", "hubo un error trabajando las imagenes")
            Log.d("Luminosidad", "" + ex.message)
        } finally {
            image.close()
        }
    }

    fun clasifyBill(image: ImageProxy){
        try {
            billetesAnalyzer.analyze(image)
        }catch(ex: Exception){
            Log.d("Scanner", "hubo un error trabajando las imagenes")
            Log.d("Clasificador", "" + ex.message)
        } finally {
            image.close()
        }
    }

}