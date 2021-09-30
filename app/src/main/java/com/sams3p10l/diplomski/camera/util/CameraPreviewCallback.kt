package com.sams3p10l.diplomski.camera.util

import android.hardware.Camera
import com.sams3p10l.diplomski.camera.CameraSource

class CameraPreviewCallback(
    private val processingRunnable: CameraSource.FrameProcessingRunnable
) : Camera.PreviewCallback {
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (data != null && camera != null)
            processingRunnable.setNextFrame(data, camera)
    }
}