package com.example.reconocimiento_billetes.presentation

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Un componente Compose para mostrar una vista previa de la cámara.
 *
 * @param controller El controlador de la cámara que gestiona el ciclo de vida y otras configuraciones.
 * @param modifier Modificador para personalizar el diseño del componente.
 * @param lifecycleOwner Propietario del ciclo de vida para vincular la cámara (opcional, usa el local por defecto).
 */
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
            }
        },
        modifier = modifier
    )

    // Vincular el controlador al ciclo de vida fuera de la configuración del PreviewView.
    controller.bindToLifecycle(lifecycleOwner)
}
