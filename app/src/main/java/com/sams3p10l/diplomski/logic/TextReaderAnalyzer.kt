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
        const val SIMILARITY_SCORE = 0.15
        const val SUBLIST_MAX_BLOCKS = 3
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
            val biggestBlocks = findBiggestBlocks(mutableBlocks)
            val blockHistogram = hashMapOf<CharSequence, Int>()
            biggestBlocks.forEach { block ->
                block.lines.forEach { line ->
                    line.elements.forEach {
                            val word = it.text
                            if (!blockHistogram.containsKey(word)) {
                                blockHistogram[word] = 1
                            } else {
                                blockHistogram[word] = blockHistogram[word]?.plus(1) as Int
                            }
                    }
                }
            }

            val similarity = similarity.cosineSimilarity(blockHistogram, latestProcessedHistogram)
            Log.d(TAG, "Similarity is $similarity")

            if (similarity < SIMILARITY_SCORE) {
                latestProcessedHistogram = blockHistogram
                val textBuilder = StringBuilder()
                biggestBlocks.forEach {
                    textBuilder.append(it.text)
                    textBuilder.append(" ")
                }
                textFoundListener(textBuilder.trim().toString())
                Log.d(TAG, "output: $textBuilder")
            }
        }
    }

    private fun findBiggestBlocks(inputList: List<Text.TextBlock>): List<Text.TextBlock> {
        inputList.sortedByDescending {
            it.text.length
        }

        val sublistSize =
            if (inputList.size >= SUBLIST_MAX_BLOCKS) SUBLIST_MAX_BLOCKS else inputList.size
        val sortedSublist = inputList.subList(0, sublistSize)

        val indices = ArrayList<Int>()
        val biggestBlocks = ArrayList<Text.TextBlock>()
        sortedSublist.forEach {
            indices.add(inputList.indexOf(it))
        }
        indices.sort()

        indices.forEach {
            biggestBlocks.add(inputList[it])
        }

        return biggestBlocks
    }

}