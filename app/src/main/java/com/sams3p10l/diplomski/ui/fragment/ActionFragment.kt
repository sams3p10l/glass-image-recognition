package com.sams3p10l.diplomski.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.databinding.FragmentActionBinding
import com.sams3p10l.diplomski.gesture.GlassGestureDetector
import com.sams3p10l.diplomski.gesture.OnSingleTapListener
import com.sams3p10l.diplomski.logic.TextReaderAnalyzer
import kotlinx.coroutines.coroutineScope
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.NoSuchElementException
import kotlin.collections.ArrayDeque


@ExperimentalGetImage
class ActionFragment : BaseFragment(), TextToSpeech.OnInitListener {
    companion object {
        val TAG = ActionFragment::class.java.name
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val STACK_SIZE = 10
    }

    private var _binding: FragmentActionBinding? = null
    private val binding: FragmentActionBinding
        get() = _binding!!

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private val blockStack = ArrayDeque<String>(STACK_SIZE)

    private var ttsEngine: TextToSpeech? = null

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    TextReaderAnalyzer(::onTextFound)
                )
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionBinding.inflate(inflater, container, false)
        dispatchCheckTtsContract.launch(Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA))
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private fun startCameraOrRequestPermissions() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted.", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun onTextFound(foundText: String) {
        Log.d(TAG, "onTextFound: $foundText")
        blockStack.add(foundText)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
                }
                cameraProviderFuture.get().bind(preview, imageAnalyzer)
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun ProcessCameraProvider.bind(preview: Preview, imageAnalyzer: ImageAnalysis) =
        try {
            unbindAll()
            bindToLifecycle(
                viewLifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
        } catch (ise: IllegalStateException) {
            Log.e(TAG, "Binding failed", ise)
        }

    private val dispatchCheckTtsContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ttsEngine = TextToSpeech(requireContext(), this)
            } else {
                Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).also { intent ->
                    startActivity(intent)
                }
            }
        }

    override fun onInit(p0: Int) {
        if (p0 != TextToSpeech.ERROR) {
            Log.d(TAG, "onInit: TTS")
            ttsEngine?.language = Locale.getDefault()
            startCameraOrRequestPermissions()
        }
    }

    override fun onSingleTap() {
        try {
            ttsEngine?.speak(blockStack.first(), TextToSpeech.QUEUE_ADD, null, null)
        } catch (e: NoSuchElementException) {
            Log.w(TAG, "onGesture: ", e)
        }
    }

}