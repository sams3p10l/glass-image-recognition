package com.sams3p10l.diplomski.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.FragmentHomeBinding
import com.sams3p10l.diplomski.gesture.GlassGestureDetector
import com.sams3p10l.diplomski.gesture.OnSingleTapListener
import com.sams3p10l.diplomski.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: BaseFragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.name
    }

    @Inject
    lateinit var actionLayoutFragment: TabLayoutFragment
    @Inject
    lateinit var settingsLayoutFragment: TabLayoutFragment
    @Inject
    lateinit var helpLayoutFragment: TabLayoutFragment

    private val fragments = arrayListOf<TabLayoutFragment>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        val screenSlidePagerAdapter = ScreenSlidePagerAdapter(requireActivity().supportFragmentManager)
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
            add(actionLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, ActionFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_action))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_action))
                }
            })
            add(settingsLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, SettingsFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_settings))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_settings))
                }
            })
            add(helpLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.FRAGMENT_KEY, HelpFragment.TAG)
                    putString(Constants.TEXT_KEY, getString(R.string.title_help))
                    putString(Constants.FOOTER_KEY, getString(R.string.subtitle_help))
                }
            })
        }
    }

    inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): TabLayoutFragment {
            return fragments[position]
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onSingleTap() {
        fragments[binding.viewpager.currentItem].onSingleTap()
    }
}