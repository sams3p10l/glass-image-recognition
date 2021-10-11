package com.sams3p10l.diplomski.logic

import android.media.Image
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import javax.inject.Inject

@ExperimentalGetImage
class TextReaderAnalyzer @Inject constructor(
    private val textFoundListener: (String) -> Unit
) : ImageAnalysis.Analyzer {
    companion object {
        val TAG = TextReaderAnalyzer::class.java.name
    }

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            process(it, imageProxy)
        }
    }

    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readTextFromImage(InputImage.fromMediaImage(image, 90), imageProxy)
        } catch (e: IOException) {
            Log.w(TAG, "Failed to load the image", e)
        }
    }

    private fun readTextFromImage(image: InputImage, imageProxy: ImageProxy) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                processTextFromImage(visionText)
                imageProxy.close()
            }
            .addOnFailureListener {
                Log.w(TAG, "Failed to process the image", it)
                imageProxy.close()
            }
    }

    private fun processTextFromImage(visionText: Text) {
        for (block in visionText.textBlocks) {
            //optimize
            for (line in block.lines) {

                for (element in line.elements) {
                    textFoundListener(element.text)
                }
            }
        }
    }

}