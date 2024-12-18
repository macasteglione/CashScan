package com.example.reconocimiento_billetes.factory

import com.example.reconocimiento_billetes.domain.Model
import com.example.reconocimiento_billetes.models.ArsModel
import com.example.reconocimiento_billetes.models.BrlModel
import com.example.reconocimiento_billetes.models.UsdModel

class ConcreteModelFactory : ModelFactory() {
    override fun createModel(modelName: String): Model = when (modelName) {
        "ars" -> ArsModel()
        "usd" -> UsdModel()
        "brl" -> BrlModel()
        else -> throw IllegalArgumentException("Unknown model name")
    }
}