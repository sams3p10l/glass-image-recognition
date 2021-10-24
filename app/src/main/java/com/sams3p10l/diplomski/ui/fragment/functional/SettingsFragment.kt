package com.sams3p10l.diplomski.ui.fragment.functional

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sams3p10l.diplomski.R
import com.sams3p10l.diplomski.databinding.FragmentSettingsBinding
import com.sams3p10l.diplomski.ui.fragment.tabs.TabLayoutFragment
import com.sams3p10l.diplomski.util.Constants
import com.sams3p10l.diplomski.util.ScreenSlidePagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val fragments = arrayListOf<TabLayoutFragment>()

    @Inject
    lateinit var tabEnglish: TabLayoutFragment

    @Inject
    lateinit var tabSerbian: TabLayoutFragment

    companion object {
        val TAG = SettingsFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)

        val screenSlidePagerAdapter =
            ScreenSlidePagerAdapter(requireActivity().supportFragmentManager, fragments)
        binding.settingsViewpager.adapter = screenSlidePagerAdapter

        populateFragmentList()
        screenSlidePagerAdapter.notifyDataSetChanged()

        binding.settingsTabLayout.setupWithViewPager(binding.settingsViewpager, true)

        return binding.root
    }

    private fun populateFragmentList() {
        fragments.apply {
            add(tabEnglish.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, getString(R.string.settings_english))
                    putString(Constants.FOOTER_KEY, getString(R.string.settings_footer))
                }
            })
            add(tabSerbian.also {
                it.arguments = Bundle().apply {
                    putString(Constants.TEXT_KEY, getString(R.string.settings_serbian))
                    putString(Constants.FOOTER_KEY, getString(R.string.settings_footer))
                }
            })
        }
    }

    override fun onSingleTap() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        val language = if (binding.settingsViewpager.currentItem == 0) "en-US" else "sr-RS"

        with (sharedPref.edit()) {
            putString(Constants.PREF_LANGUAGE_KEY, language)
            apply()
        }

        findNavController().popBackStack()
    }

}