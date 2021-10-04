package com.sams3p10l.diplomski.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.FragmentHomeBinding
import com.sams3p10l.diplomski.util.Constants
import com.sams3p10l.diplomski.util.Constants.FOOTER_KEY
import com.sams3p10l.diplomski.util.Constants.TEXT_KEY

class HomeFragment : Fragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.name
    }

    private lateinit var binding: FragmentHomeBinding

    private lateinit var function: String

    private var cameraPermissionStatus = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dispatchRequestPermissionContract.launch(android.Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        function = arguments?.getString(TEXT_KEY).orEmpty().also { binding.hfTitle.text = it }
        binding.hfFooter.text = arguments?.getString(FOOTER_KEY).orEmpty()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tag: String
        val destinationClass: Fragment
        when (function) {
            Constants.ACTION_FRAGMENT_TITLE -> {
                tag = ActionFragment.TAG
                destinationClass = ActionFragment()
            }
            Constants.SETTINGS_FRAGMENT_TITLE -> {
                tag = SettingsFragment.TAG
                destinationClass = SettingsFragment()
            }
            Constants.HELP_FRAGMENT_TITLE -> {
                tag = HelpFragment.TAG
                destinationClass = HelpFragment()
            }
            else -> {
                tag = HelpFragment.TAG
                destinationClass = HelpFragment()
            }
        }

        binding.root.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(it.id, destinationClass, tag)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private val dispatchRequestPermissionContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraPermissionStatus = true
            } else {
                cameraPermissionStatus = false
                Log.w(ActionFragment.TAG, "Permission denied")
            }
        }

}