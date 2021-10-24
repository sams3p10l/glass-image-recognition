package com.sams3p10l.diplomski.ui.fragment.tabs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.FragmentHomeBinding
import com.sams3p10l.diplomski.ui.fragment.functional.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.functional.BaseFragment
import com.sams3p10l.diplomski.ui.fragment.functional.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.functional.SettingsFragment
import com.sams3p10l.diplomski.util.Constants
import com.sams3p10l.diplomski.util.ScreenSlidePagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.name
    }

    @Inject
    lateinit var tabAction: TabLayoutFragment

    @Inject
    lateinit var tabSettings: TabLayoutFragment

    @Inject
    lateinit var tabHelp: TabLayoutFragment

    private val fragments = arrayListOf<TabLayoutFragment>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        val screenSlidePagerAdapter =
            ScreenSlidePagerAdapter(requireActivity().supportFragmentManager, fragments)
        binding.viewpager.adapter = screenSlidePagerAdapter

        populateFragmentList()
        screenSlidePagerAdapter.notifyDataSetChanged()

        binding.tabLayout.setupWithViewPager(binding.viewpager, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun populateFragmentList() {
        fragments.apply {
            add(tabAction.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, ActionFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_action))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_action))
                }
            })
            add(tabSettings.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, SettingsFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_settings))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_settings))
                }
            })
            add(tabHelp.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, HelpFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_help))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_help))
                }
            })
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onSingleTap() {
        fragments[binding.viewpager.currentItem].onSingleTap()
    }
}