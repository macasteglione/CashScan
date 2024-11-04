package com.example.reconocimiento_billetes.ui.theme

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.reconocimiento_billetes.CountBillActivity
import com.example.reconocimiento_billetes.IntroActivity
import com.example.reconocimiento_billetes.R
import com.example.reconocimiento_billetes.ScanBillActivity
import com.example.reconocimiento_billetes.presentation.getCurrentDateTime

@Composable
private fun BotonCountBill(context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                val intent = Intent(context, CountBillActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Ir a Historial de Billetes")
        }
    }
}

@Composable
private fun FondoImagen() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0f)
    )
}

@Composable
private fun BotonScanBill(context: Context) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                val intent = Intent(context, ScanBillActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Escanear Billete")
        }
    }
}

@Composable
private fun BotonTutorial(context: Context) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                val intent = Intent(context, IntroActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Tutorial")
        }
    }
}

@Composable
fun MainActivityTheme(context: Context) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val percentage = 0.45f
    val thresholdWidth = with(LocalDensity.current) { screenWidth.toPx() * percentage }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX in -thresholdWidth..thresholdWidth) offsetX = 0f
                    },
                    onDragCancel = { offsetX = 0f }
                ) { _, dragAmount ->
                    offsetX += dragAmount.x

                    when {
                        offsetX < -thresholdWidth -> {
                            offsetX = 0f
                            val intent = Intent(context, CountBillActivity::class.java)
                            context.startActivity(intent)
                        }

                        offsetX > thresholdWidth -> {
                            offsetX = 0f
                            val intent = Intent(context, ScanBillActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
            }
    ) {
        FondoImagen()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .align(Alignment.Center)
        ) {
            BotonCountBill(context)
            BotonScanBill(context)
            //BotonTutorial(context)
        }
    }
}