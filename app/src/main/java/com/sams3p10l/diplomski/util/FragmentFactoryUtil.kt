package com.sams3p10l.diplomski.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sams3p10l.diplomski.ui.fragment.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.SettingsFragment
import com.sams3p10l.diplomski.util.Constants.FOOTER_KEY
import com.sams3p10l.diplomski.util.Constants.TEXT_KEY

object FragmentFactoryUtil {
    fun create(fragmentClass: String, text: String, footer: String): Fragment {
        val fragment = when(fragmentClass) {
            ActionFragment.TAG -> ActionFragment()
            HelpFragment.TAG -> HelpFragment()
            SettingsFragment.TAG -> SettingsFragment()
            else -> Fragment()
        }

        val args = Bundle().apply {
            putString(TEXT_KEY, text)
            putString(FOOTER_KEY, footer)
        }

        fragment.arguments = args
        return fragment
    }
}