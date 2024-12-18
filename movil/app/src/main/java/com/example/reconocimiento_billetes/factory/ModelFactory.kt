package com.example.reconocimiento_billetes.factory

import com.example.reconocimiento_billetes.domain.Model

abstract class ModelFactory {
    abstract fun createModel(modelName: String): Model
}