package com.sams3p10l.diplomski.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.databinding.FragmentHelpBinding

class HelpFragment : BaseFragment() {
    companion object {
        val TAG = HelpFragment::class.java.name
    }

    private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater)
        return binding.root
    }

    override fun onSingleTap() {
        TODO("Not yet implemented")
    }

}