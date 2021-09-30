package com.sams3p10l.diplomski.textdetection

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.sams3p10l.diplomski.camera.FrameMetadata
import com.sams3p10l.diplomski.camera.GraphicOverlay
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class TextRecognitionProcessor @Inject constructor() {
    companion object {
        val TAG = TextRecognitionProcessor::class.java.name
    }

    private val shouldThrottle: AtomicBoolean = AtomicBoolean(false)

    @Inject
    private lateinit var recognizer: TextRecognizer

    fun process(data: ByteBuffer?, frameMetadata: FrameMetadata, overlay: GraphicOverlay) {
        if (shouldThrottle.get())
            return

        if (data != null) {
            val image = InputImage.fromByteBuffer(
                data,
                frameMetadata.width,
                frameMetadata.height,
                frameMetadata.rotation,
                frameMetadata.cameraFacing
            )

            detectInImage(image, overlay)
        }
    }

    fun stop() {
        recognizer.close()
    }

    private fun detectInImage(image: InputImage, overlay: GraphicOverlay) {
        recognizer.process(image)
            .addOnCompleteListener {
                shouldThrottle.set(false)
                onDetectComplete(it.result, overlay)
            }
            .addOnFailureListener {
                shouldThrottle.set(false)
                onDetectFailure(it)
            }

        shouldThrottle.set(true)
    }

    private fun onDetectComplete(result: Text, overlay: GraphicOverlay) {
        //overlay.clear()
        val blocks = result.textBlocks

        for (i in 0..blocks.size) {
            val lines = blocks[i].lines

            for (j in 0..lines.size) {
                val elements = lines[j].elements

                for (k in 0..elements.size) {
                    val textGraphic = TextGraphic()
                    //overlay.add(textGraphic)
                }
            }
        }
    }

    private fun onDetectFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.", e)
    }

}