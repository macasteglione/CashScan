package com.example.reconocimiento_billetes.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.example.reconocimiento_billetes.data.SQLiteHelper
import java.util.Calendar
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

fun vibrateDevice(vibrator: Vibrator, duration: Long = 300L) {
    if (vibrator.hasVibrator()) vibrator.vibrate(duration)
}

fun getCurrentDateTime(): String {
    val currentDateTime = Calendar.getInstance().time
    val dateFormat = java.text.DateFormat.getDateTimeInstance(
        java.text.DateFormat.DEFAULT,
        java.text.DateFormat.DEFAULT
    )
    return dateFormat.format(currentDateTime)
}

fun hasCameraPermission(context: Context) = ContextCompat.checkSelfPermission(
    context, Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED

fun guardarBaseDeDatos(billete: String, context: Context) {
    val db = SQLiteHelper(context)
    db.insertBill(billete.toInt(), getCurrentDateTime())
}