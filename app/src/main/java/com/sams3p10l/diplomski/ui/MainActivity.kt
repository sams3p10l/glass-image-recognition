package com.sams3p10l.diplomski.ui

import android.os.Bundle
import androidx.camera.core.ExperimentalGetImage
import androidx.core.util.rangeTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sams3p10l.diplomski.databinding.ActivityMainBinding
import com.sams3p10l.diplomski.gesture.GlassGestureDetector
import com.sams3p10l.diplomski.ui.fragment.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.BaseFragment
import com.sams3p10l.diplomski.ui.fragment.HomeFragment
import com.sams3p10l.diplomski.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    @Inject
    lateinit var actionLayoutFragment: HomeFragment
    @Inject
    lateinit var settingsLayoutFragment: HomeFragment
    @Inject
    lateinit var helpLayoutFragment: HomeFragment

    private lateinit var binding: ActivityMainBinding
    private val fragments = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val screenSlidePagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        binding.viewpager.adapter = screenSlidePagerAdapter

        populateFragmentList()
        screenSlidePagerAdapter.notifyDataSetChanged()

        binding.tabLayout.setupWithViewPager(binding.viewpager, true)
    }

    private fun populateFragmentList() {
        fragments.apply {
            add(actionLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, "Action")
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
            add(settingsLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, "Settings")
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
            add(helpLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, "Help")
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
        }
    }

    @ExperimentalGetImage
    override fun onGesture(gesture: GlassGestureDetector.Gesture?): Boolean {
        return when (gesture) {
            GlassGestureDetector.Gesture.TAP -> {
                val currentFragment = supportFragmentManager.fragments.last()
                if (currentFragment is BaseFragment) {
                    currentFragment.onSingleTap()
                    true
                } else {
                    super.onGesture(gesture)
                }
            }
            else -> super.onGesture(gesture)
        }
    }

    inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }
    }
}