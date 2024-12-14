package com.example.reconocimiento_billetes.domain

/**
 * Representa los datos de un billete reconocidos por la aplicación.
 *
 * @property value El valor monetario del billete.
 * @property date La fecha en que se reconoció el billete, en formato de cadena.
 */
data class BillData(
    val value: Int,
    val date: String
) {
    init {
        require(value > 0) { "El valor del billete debe ser mayor que cero." }
        require(date.isNotEmpty()) { "La fecha no puede estar vacía." }
    }
}
