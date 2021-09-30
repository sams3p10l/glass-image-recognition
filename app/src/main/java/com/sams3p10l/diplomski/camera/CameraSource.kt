package com.sams3p10l.diplomski.camera

import android.app.Activity
import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.util.Size
import android.view.Surface
import com.sams3p10l.diplomski.camera.util.CameraPreviewCallback
import com.sams3p10l.diplomski.camera.util.SizePair
import com.sams3p10l.diplomski.textdetection.TextRecognitionProcessor
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil

@SuppressWarnings("deprecation")
@AndroidEntryPoint
class CameraSource @Inject constructor(
    val activity: Activity,
    val overlay: GraphicOverlay
) {

    @Inject
    lateinit var processor: TextRecognitionProcessor

    private val processorLock = Object()
    private var processingRunnable: FrameProcessingRunnable
    private val bytesToByteBuffer: IdentityHashMap<ByteArray, ByteBuffer> = IdentityHashMap()

    private lateinit var previewSize: Size
    private lateinit var camera: Camera

    private var rotation = 0
    private var facing = CAMERA_FACING_BACK

    init {
        overlay.clear()
        processingRunnable = FrameProcessingRunnable()
    }

//    fun start(): CameraSource {
//        if (camera != null)
//            return this
//
//
//    }

    private fun createCamera(): Camera? {
        val requestedCameraId = getIdForRequestedCamera(facing)
        if (requestedCameraId == -1) {
            Log.e(TAG, "Could not find requested camera")
            return null
        }

        camera = Camera.open(requestedCameraId)
        val sizePair = selectSizePair(camera)
        if (sizePair == null) {
            Log.e(TAG, "Could not find suitable preview size")
            return null
        }
        val pictureSize = sizePair.pictureSize
        this.previewSize = sizePair.previewSize

        val previewFpsRange = selectPreviewFpsRange(camera)
        if (previewFpsRange == null) {
            Log.e(TAG, "Could not find suitable preview frames per second range")
        }

        val params = camera.parameters
        if (pictureSize != null) {
            params.setPictureSize(pictureSize.width, pictureSize.height)
        }
        params.setPreviewSize(previewSize.width, previewSize.height)

        val minFpsRange = previewFpsRange?.get(Camera.Parameters.PREVIEW_FPS_MIN_INDEX)
        val maxFpsRange = previewFpsRange?.get(Camera.Parameters.PREVIEW_FPS_MAX_INDEX)
        if (minFpsRange != null && maxFpsRange != null) {
            params.setPreviewFpsRange(minFpsRange, maxFpsRange)
        }
        params.previewFormat = ImageFormat.NV21

        setRotation(camera, params, requestedCameraId)

        if (REQUESTED_AUTOFOCUS) {
            if (params.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            } else {
                Log.i(TAG, "Camera auto focus isn't supported on this device.")
            }
        }
        camera.parameters = params

        // Four frame buffers are needed for working with the camera:
        //
        //   one for the frame that is currently being executed upon in doing detection
        //   one for the next pending frame to process immediately upon completing detection
        //   two for the frames that the camera uses to populate future preview images
        //
        // Through trial and error it appears that two free buffers, in addition to the two buffers
        // used in this code, are needed for the camera to work properly.  Perhaps the camera has
        // one thread for acquiring images, and another thread for calling into user code.  If only
        // three buffers are used, then the camera will spew thousands of warning messages when
        // detection takes a non-trivial amount of time.
        camera.setPreviewCallbackWithBuffer(CameraPreviewCallback(processingRunnable))
        for (i in 0..3) {
            camera.addCallbackBuffer(createPreviewBuffer(previewSize))
        }

        return camera
    }

    /**
     * Calculates the correct rotation for the given camera id and sets the rotation in the
     * parameters. It also sets the camera's display orientation and rotation.
     *
     * @param parameters the camera parameters for which to set the rotation
     * @param cameraId the camera id to set rotation based on
     */
    private fun setRotation(camera: Camera, params: Camera.Parameters, cameraId: Int) {
        val windowManager =
            activity.windowManager //activity.getSystemService(Context.WINDOW_SERVICE)
        var degrees = 0
        val rotation = windowManager.defaultDisplay.rotation

        degrees = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> Log.e(TAG, "Bad rotation value: $rotation")
        }

        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)

        var angle = 0
        var displayAngle = 0
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360
            displayAngle = (360 - angle) % angle
        } else {
            angle = (cameraInfo.orientation - degrees) % 360
            displayAngle = angle
        }

        this.rotation = angle / 90
        camera.setDisplayOrientation(displayAngle)
        params.setRotation(angle)
    }

    private fun createPreviewBuffer(previewSize: Size): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21)
        val sizeInBits: Long = (previewSize.height * previewSize.width * bitsPerPixel).toLong()
        val bufferSize = (ceil(sizeInBits / 8.0) + 1).toInt()

        val byteArray = ByteArray(bufferSize)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        if (!byteBuffer.hasArray() || !byteBuffer.array().contentEquals(byteArray)) {
            throw IllegalStateException("Failed to create valid buffer")
        }

        bytesToByteBuffer[byteArray] = byteBuffer
        return byteArray
    }

    inner class FrameProcessingRunnable : Runnable {
        private val lock = Object()
        private var active = true

        private var pendingFrameData: ByteBuffer? = null

        override fun run() {
            var data: ByteBuffer?

            while (true) {
                synchronized(lock) {
                    while (active && pendingFrameData == null) {
                        try {
                            lock.wait()
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Frame processing loop terminated", e)
                            return
                        }
                    }

                    if (!active)
                        return

                    data = pendingFrameData
                    pendingFrameData = null
                }

                try {
                    synchronized(processorLock) {
                        Log.d(TAG, "Processing image")
                        processor.process(
                            data,
                            FrameMetadata(
                                previewSize.width,
                                previewSize.height,
                                rotation,
                                facing
                            ),
                            overlay
                        )
                    }
                } catch (t: Throwable) {
                    Log.e(TAG, "Exception from receiver", t)
                } finally {
                    camera?.addCallbackBuffer(data?.array())
                }
            }
        }

        fun release() {

        }

        fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }

        fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(lock) {
                if (pendingFrameData != null) {
                    camera.addCallbackBuffer(pendingFrameData?.array())
                    pendingFrameData = null
                }

                if (!bytesToByteBuffer.containsKey(data)) {
                    Log.d(
                        TAG, "Skipping frame. Could not find ByteBuffer associated with the image "
                                + "data from the camera."
                    )
                    return
                }

                pendingFrameData = bytesToByteBuffer[data]
                lock.notifyAll()
            }
        }

    }

    private fun cleanScreen() {
        overlay.clear()
    }

    companion object {
        val TAG: String = CameraSource::class.java.name

        const val CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK
        const val CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT
        const val REQUESTED_PREVIEW_WIDTH = 1280
        const val REQUESTED_PREVIEW_HEIGHT = 960
        const val REQUESTED_FPS = 20.0f
        const val REQUESTED_AUTOFOCUS = true

        /**
         * If the absolute difference between a preview size aspect ratio and a picture size aspect ratio
         * is less than this tolerance, they are considered to be the same aspect ratio.
         */
        private const val ASPECT_RATIO_TOLERANCE = 0.01f

        private fun getIdForRequestedCamera(facing: Int): Int {
            val cameraInfo = Camera.CameraInfo()

            for (i in 0..Camera.getNumberOfCameras()) {
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == facing) {
                    return i
                }
            }

            return -1
        }

        /**
         * Selects the most suitable preview and picture size, given the desired width and height.
         *
         * <p>Even though we only need to find the preview size, it's necessary to find both the preview
         * size and the picture size of the camera together, because these need to have the same aspect
         * ratio. On some hardware, if you would only set the preview size, you will get a distorted
         * image.
         *
         * @param camera the camera to select a preview size from
         * @return the selected preview and picture size pair
         */
        private fun selectSizePair(camera: Camera): SizePair? {
            val validPreviewSizes = generateValidPreviewSizeList(camera)

            var selectedPair: SizePair? = null
            var minDiff = Int.MAX_VALUE
            for (sizePair: SizePair in validPreviewSizes) {
                val size = sizePair.previewSize
                val diff =
                    abs(size.width - REQUESTED_PREVIEW_WIDTH) + abs(size.height - REQUESTED_PREVIEW_HEIGHT)
                if (diff < minDiff) {
                    selectedPair = sizePair
                    minDiff = diff
                }
            }

            return selectedPair
        }

        /**
         * Generates a list of acceptable preview sizes. Preview sizes are not acceptable if there is not
         * a corresponding picture size of the same aspect ratio. If there is a corresponding picture size
         * of the same aspect ratio, the picture size is paired up with the preview size.
         *
         * <p>This is necessary because even if we don't use still pictures, the still picture size must
         * be set to a size that is the same aspect ratio as the preview size we choose. Otherwise, the
         * preview images may be distorted on some devices.
         */
        private fun generateValidPreviewSizeList(camera: Camera): MutableList<SizePair> {
            val parameters = camera.parameters
            val supportedPreviewSizes = parameters.supportedPreviewSizes
            val supportedPictureSizes = parameters.supportedPictureSizes
            val validPreviewSizes: MutableList<SizePair> = ArrayList()

            for (previewSize in supportedPreviewSizes) {
                val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()

                // By looping through the picture sizes in order, we favor the higher resolutions.
                // We choose the highest resolution in order to support taking the full resolution
                // picture later.
                for (pictureSize in supportedPictureSizes) {
                    val pictureAspectRatio =
                        pictureSize.width.toFloat() / pictureSize.height.toFloat()
                    if (abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                        validPreviewSizes.add(SizePair(previewSize, pictureSize))
                        break
                    }
                }
            }

            // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all
            // of the preview sizes and hope that the camera can handle it.  Probably unlikely, but we
            // still account for it.
            if (validPreviewSizes.size == 0) {
                Log.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size")
                for (previewSize in supportedPreviewSizes) {
                    // The null picture size will let us know that we shouldn't set a picture size.
                    validPreviewSizes.add(SizePair(previewSize, null))
                }
            }

            return validPreviewSizes
        }

        /**
         * Selects the most suitable preview frames per second range, given the desired frames per second.
         *
         * @param camera the camera to select a frames per second range from
         * @param desiredPreviewFps the desired frames per second for the camera preview frames
         * @return the selected preview frames per second range
         */
        private fun selectPreviewFpsRange(camera: Camera): IntArray? {
            val desiredPreviewFpsScaled = (REQUESTED_FPS * 1000.0f).toInt()

            // The method for selecting the best range is to minimize the sum of the differences between
            // the desired value and the upper and lower bounds of the range.  This may select a range
            // that the desired value is outside of, but this is often preferred.  For example, if the
            // desired frame rate is 29.97, the range (30, 30) is probably more desirable than the
            // range (15, 30).
            var selectedFpsRange: IntArray? = null
            var minDiff = Int.MAX_VALUE
            val previewFpsRangeList = camera.parameters.supportedPreviewFpsRange

            for (range in previewFpsRangeList) {
                val deltaMin =
                    desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val deltaMax =
                    desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                val diff = abs(deltaMin) + abs(deltaMax)

                if (diff < minDiff) {
                    selectedFpsRange = range
                    minDiff = diff
                }
            }

            return selectedFpsRange
        }

    }

}