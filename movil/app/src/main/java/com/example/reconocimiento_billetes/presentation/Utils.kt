package com.example.reconocimiento_billetes.presentation

import android.content.Context
import com.example.reconocimiento_billetes.ml.Ars
import java.util.Locale

fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}

fun getLocalizedAudioResId(context: Context, baseFileName: String): Int {
    val language = getDeviceLanguage()

    val localizedFileName = "${baseFileName}_${language}"
    val localizedResId =
        context.resources.getIdentifier(localizedFileName, "raw", context.packageName)

    return if (localizedResId != 0) localizedResId
    else {
        val defaultFileName = "${baseFileName}_en"
        context.resources.getIdentifier(defaultFileName, "raw", context.packageName)
    }
}

fun loadModel(modelName: String, context: Context): Ars {
    return when (modelName) {
        "ars" -> Ars.newInstance(context)
        else -> throw IllegalArgumentException("Invalid model name: $modelName")
    }
}

fun loadClassNames(context: Context, baseFileName: String): List<String> {
    return context.assets.open("${baseFileName}_labels.txt").bufferedReader().readLines()
}