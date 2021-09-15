package com.sams3p10l.diplomski.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.databinding.FragmentHomeBinding
import com.sams3p10l.diplomski.util.Constants.FOOTER_KEY
import com.sams3p10l.diplomski.util.Constants.TEXT_KEY

class HomeFragment : Fragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.name
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.hfTitle.text = arguments?.getString(TEXT_KEY).orEmpty()
        binding.hfFooter.text = arguments?.getString(FOOTER_KEY).orEmpty()

        return binding.root
    }
}