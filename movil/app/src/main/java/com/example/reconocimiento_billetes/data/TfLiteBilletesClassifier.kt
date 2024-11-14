package com.example.reconocimiento_billetes.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import com.example.reconocimiento_billetes.domain.BilletesClassifier
import com.example.reconocimiento_billetes.domain.Classification
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions

class TfLiteBilletesClassifier(
    private val context: Context,
    private val threshold: Float = 0.95f,
    private val maxResults: Int = 1,
) : BilletesClassifier {

    // Lazy initialization del clasificador
    private val classifier: ImageClassifier by lazy {
        val baseOptions = BaseOptions.builder().setNumThreads(2).build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()


        //intercambiar modelo.


        try {
            ImageClassifier.createFromFileAndOptions(
                context,
                "model_unquant.tflite",
                options
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            throw e // Asegura que si ocurre un error al cargar el modelo, este se propague.
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = classifier.classify(tensorImage, imageProcessingOptions)
        Log.d("ClassifierResults", results.toString())

        return results.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    name = category.displayName,
                    score = category.score,
                    index = category.index
                )
            }
        }.distinctBy { it.name } ?: emptyList()
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}
