package com.sams3p10l.diplomski.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import javax.inject.Inject

@AndroidEntryPoint
class ActionFragment : Fragment() {
    companion object {
        val TAG = ActionFragment::class.java.name
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    @Inject
    lateinit var processor: TextProcessor

    private var _binding: FragmentActionBinding? = null
    private val binding: FragmentActionBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dispatchTakePictureIntent.launch(null)
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
            processor.processImage(bitmap)
        }
}