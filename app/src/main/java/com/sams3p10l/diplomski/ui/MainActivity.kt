package com.sams3p10l.diplomski.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sams3p10l.diplomski.databinding.ActivityMainBinding
import com.sams3p10l.diplomski.ui.fragment.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.SettingsFragment
import com.sams3p10l.diplomski.util.FragmentFactoryUtil

class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

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
            add(FragmentFactoryUtil.create(ActionFragment.TAG, "Action", "Test"))
            add(FragmentFactoryUtil.create(SettingsFragment.TAG, "Settings", "Test"))
            add(FragmentFactoryUtil.create(HelpFragment.TAG, "Action", "Test"))
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