package com.sams3p10l.diplomski.logic

import android.media.Image
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.apache.commons.text.similarity.CosineSimilarity
import java.io.IOException
import javax.inject.Inject

@ExperimentalGetImage
class TextReaderAnalyzer @Inject constructor(
    private val textFoundListener: (String) -> Unit
) : ImageAnalysis.Analyzer {
    companion object {
        val TAG = TextReaderAnalyzer::class.java.name
        const val SIMILARITY_SCORE = 0.2
    }

    private var latestProcessedHistogram = hashMapOf<CharSequence, Int>()
    private val similarity: CosineSimilarity by lazy { CosineSimilarity() }

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            process(it, imageProxy)
        }
    }

    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readTextFromImage(InputImage.fromMediaImage(image, 0), imageProxy)
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
        val mutableBlocks = visionText.textBlocks.toMutableList()

        if (mutableBlocks.isNotEmpty()) {
            mutableBlocks.sortByDescending {
                it.text.length
            }

            val biggestBlock = mutableBlocks[0]
            val blockHistogram = hashMapOf<CharSequence, Int>()
            biggestBlock.lines.forEach { line ->
                line.elements.forEach {
                    val word = it.text
                    if (!blockHistogram.containsKey(word)) {
                        blockHistogram[word] = 1
                    } else {
                        blockHistogram[word] = blockHistogram[word]?.plus(1) as Int
                    }
                }
            }

            val similarity = similarity.cosineSimilarity(blockHistogram, latestProcessedHistogram)
            Log.d(TAG, "Similarity is $similarity")

            if (similarity < SIMILARITY_SCORE) {
                latestProcessedHistogram = blockHistogram
                textFoundListener(biggestBlock.text)
            }
        }
    }

}