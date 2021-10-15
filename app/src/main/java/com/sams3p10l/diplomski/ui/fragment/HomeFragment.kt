package com.sams3p10l.diplomski.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.databinding.FragmentHomeBinding
import com.sams3p10l.diplomski.gesture.OnSingleTapListener
import com.sams3p10l.diplomski.util.Constants.FOOTER_KEY
import com.sams3p10l.diplomski.util.Constants.TEXT_KEY


@ExperimentalGetImage
class HomeFragment : BaseFragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.name
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var function: String

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

    override fun onSingleTap() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(binding.root.id, ActionFragment(), ActionFragment.TAG)
            ?.addToBackStack(null)
            ?.commit()
    }
}