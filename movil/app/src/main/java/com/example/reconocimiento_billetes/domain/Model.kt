package com.example.reconocimiento_billetes.domain

import com.chaquo.python.PyObject

interface Model {
    fun process(imagePath: String): PyObject?
}
