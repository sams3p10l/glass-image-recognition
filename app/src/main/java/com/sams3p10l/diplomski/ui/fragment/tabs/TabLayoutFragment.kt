package com.sams3p10l.diplomski.ui.fragment.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ExperimentalGetImage
import androidx.navigation.fragment.findNavController
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.FragmentBaseLayoutBinding
import com.sams3p10l.diplomski.ui.fragment.functional.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.functional.BaseFragment
import com.sams3p10l.diplomski.ui.fragment.functional.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.functional.SettingsFragment
import com.sams3p10l.diplomski.util.Constants.FOOTER_KEY
import com.sams3p10l.diplomski.util.Constants.FRAGMENT_KEY
import com.sams3p10l.diplomski.util.Constants.TEXT_KEY


@ExperimentalGetImage
class TabLayoutFragment : BaseFragment() {
    companion object {
        val TAG: String = TabLayoutFragment::class.java.name
    }

    private lateinit var function: String
    private lateinit var fragmentKey: String
    private lateinit var binding: FragmentBaseLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseLayoutBinding.inflate(inflater)

        function = arguments?.getString(TEXT_KEY).orEmpty().also { binding.hfTitle.text = it }
        binding.hfFooter.text = arguments?.getString(FOOTER_KEY).orEmpty()
        fragmentKey = arguments?.getString(FRAGMENT_KEY).orEmpty()

        return binding.root
    }

    override fun onSingleTap() {
        val destination = when (fragmentKey) {
            ActionFragment.TAG -> R.id.action_homeFragment_to_actionFragment
            SettingsFragment.TAG -> R.id.action_homeFragment_to_settingsFragment
            HelpFragment.TAG -> R.id.action_homeFragment_to_helpFragment
            else -> null
        }

        destination?.let {
            findNavController().navigate(it)
        }
    }

}