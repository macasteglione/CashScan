package com.example.reconocimiento_billetes.domain

data class Classification(
    val name: String,
    val score: Float,
    val index: Int,
)
