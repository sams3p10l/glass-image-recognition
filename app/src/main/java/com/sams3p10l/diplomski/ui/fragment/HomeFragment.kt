package com.sams3p10l.diplomski.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sams3p10l.diplomski.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val tabLayout = view?.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view?.findViewById<ViewPager2>(R.id.pager)

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Action"
                        1 -> tab.text = "Settings"
                    }
            }.attach()
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}