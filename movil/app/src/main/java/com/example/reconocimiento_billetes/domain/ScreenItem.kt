package com.example.reconocimiento_billetes.domain

/**
 * Representa un elemento de la pantalla en el tutorial de la aplicación.
 *
 * @property title El título del elemento de la pantalla.
 * @property description Una breve descripción del elemento.
 * @property audio El identificador del recurso de audio asociado.
 */
data class ScreenItem(
    val title: String,
    val description: String,
    val audio: Int
) {
    init {
        require(title.isNotEmpty()) { "El título no puede estar vacío." }
        require(description.isNotEmpty()) { "La descripción no puede estar vacía." }
        require(audio > 0) { "El identificador de audio debe ser un número positivo válido." }
    }
}
