package com.sams3p10l.diplomski.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.databinding.FragmentActionBinding
import com.sams3p10l.diplomski.processing.TextProcessor
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ActionFragment : Fragment(), TextToSpeech.OnInitListener {
    companion object {
        val TAG = ActionFragment::class.java.name
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    @Inject
    lateinit var processor: TextProcessor

    private var _binding: FragmentActionBinding? = null
    private val binding: FragmentActionBinding
        get() = _binding!!

    private var ttsEngine: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dispatchCheckTtsContract.launch(Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val dispatchTakePictureIntent =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            processor.processImage(bitmap, ttsEngine)
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
            dispatchTakePictureIntent.launch(null)
        }
    }

}