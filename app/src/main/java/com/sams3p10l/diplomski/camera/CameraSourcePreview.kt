package com.sams3p10l.diplomski.camera

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.inline.InlineContentView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraSourcePreview @Inject constructor(
    context: Context,
    attrs: AttributeSet
) : ViewGroup(context, attrs) {

    private var startRequested: Boolean = false
    private var surfaceAvailable: Boolean = false

    @Inject
    private lateinit var surfaceView: SurfaceView

    private var cameraSource: CameraSource? = null
    private lateinit var overlay: GraphicOverlay

    init {
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null)
            stop()

        this.cameraSource = cameraSource

        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    fun start(cameraSource: CameraSource, overlay: GraphicOverlay) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        cameraSource
    }

    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {

        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    private inner class SurfaceCallback() : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            surfaceAvailable = true
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            TODO("Not yet implemented")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            TODO("Not yet implemented")
        }

    }
}