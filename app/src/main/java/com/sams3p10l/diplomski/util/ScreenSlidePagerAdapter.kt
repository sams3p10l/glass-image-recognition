package com.sams3p10l.diplomski.util

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sams3p10l.diplomski.ui.fragment.tabs.TabLayoutFragment

class ScreenSlidePagerAdapter(
    fm: FragmentManager,
    private val fragments: List<TabLayoutFragment>
    ): FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): TabLayoutFragment {
        return fragments[position]

    }
}