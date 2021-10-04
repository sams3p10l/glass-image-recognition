package com.sams3p10l.diplomski.processing

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TextProcessor @Inject constructor(@ApplicationContext val context: Context) {
    companion object {
        val TAG: String = TextProcessor::class.java.name
    }

    private var recognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processImage(bitmap: Bitmap, ttsEngine: TextToSpeech?) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                ttsEngine?.speak(
                    processResult(visionText),
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            }
            .addOnFailureListener {
                Log.e(TAG, "processImage: ", it)
            }
    }

    private fun processResult(result: Text): CharSequence {
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            for (line in block.lines) {
                val lineText = line.text
                for (element in line.elements) {
                    val elementText = element.text
                }
            }
        }

        return resultText
    }

}