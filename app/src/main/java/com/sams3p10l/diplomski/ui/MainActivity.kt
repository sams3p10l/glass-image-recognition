package com.sams3p10l.diplomski.ui

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sams3p10l.diplomski.databinding.ActivityMainBinding
import com.sams3p10l.diplomski.ui.fragment.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.HomeFragment
import com.sams3p10l.diplomski.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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
                    putString(Constants.TEXT_KEY, Constants.ACTION_FRAGMENT_TITLE)
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
            add(settingsLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, Constants.SETTINGS_FRAGMENT_TITLE)
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
            add(helpLayoutFragment.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, Constants.HELP_FRAGMENT_TITLE)
                    putString(Constants.FOOTER_KEY, "Test")
                }
            })
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