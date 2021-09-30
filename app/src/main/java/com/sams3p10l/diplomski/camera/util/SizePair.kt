package com.sams3p10l.diplomski.camera.util

import android.hardware.Camera
import android.util.Size

/**
 * Stores a preview size and a corresponding same-aspect-ratio picture size. To avoid distorted
 * preview images on some devices, the picture size must be set to a size that is the same aspect
 * ratio as the preview size or the preview may end up being distorted. If the picture size is
 * null, then there is no picture size with the same aspect ratio as the preview size.
 */
class SizePair(
    previewSize: Camera.Size,
    pictureSize: Camera.Size?
) {
    private val preview: Size
    private var picture: Size? = null

    val previewSize: Size
        get() = preview

    val pictureSize: Size?
        get() = picture

    init {
        preview = Size(previewSize.width, previewSize.height)
        pictureSize?.let {
            picture = Size(it.width, it.height)
        }
    }
}