package com.example.reconocimiento_billetes.models

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.reconocimiento_billetes.domain.Model

class ArsModel : Model {
    override fun process(imagePath: String): PyObject? {
        val py = Python.getInstance()
        val myModule = py.getModule("main")
        val classifyImage = myModule["ars"]
        return classifyImage?.call(imagePath)
    }
}