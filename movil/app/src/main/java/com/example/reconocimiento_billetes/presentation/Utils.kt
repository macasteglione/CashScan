package com.example.reconocimiento_billetes.presentation

import android.content.Context
import android.util.Log
import java.util.Locale

fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}

fun getLocalizedAudioResId(context: Context, baseFileName: String): Int {
    val language = getDeviceLanguage()
    Log.d("Idioma", "getLocalizedAudioResId: $language")

    val localizedFileName = "${baseFileName}_${language}"
    val localizedResId =
        context.resources.getIdentifier(localizedFileName, "raw", context.packageName)

    return if (localizedResId != 0) {
        localizedResId
    } else {
        val defaultFileName = "${baseFileName}_en" // Inglés predeterminado
        Log.d("Idioma", "Archivo no encontrado para $language, usando predeterminado en inglés")
        context.resources.getIdentifier(defaultFileName, "raw", context.packageName)
    }
}
