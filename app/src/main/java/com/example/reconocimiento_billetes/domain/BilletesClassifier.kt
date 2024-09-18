package com.example.reconocimiento_billetes.domain

import android.graphics.Bitmap

interface BilletesClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}