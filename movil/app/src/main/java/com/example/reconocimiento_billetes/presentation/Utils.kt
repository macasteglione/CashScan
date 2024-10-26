package com.example.reconocimiento_billetes.presentation

import java.util.Calendar
import java.util.Locale

fun getCurrentDateTime(): String {
    val currentDateTime = Calendar.getInstance().time
    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentDateTime)
}