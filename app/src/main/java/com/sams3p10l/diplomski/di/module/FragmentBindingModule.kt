package com.sams3p10l.diplomski.di.module

import android.annotation.SuppressLint
import com.sams3p10l.diplomski.ui.fragment.functional.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.functional.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.tabs.TabLayoutFragment
import com.sams3p10l.diplomski.ui.fragment.functional.SettingsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
@SuppressLint("UnsafeOptInUsageError")
object FragmentBindingModule {

    @Provides
    fun provideHomeFragment() = TabLayoutFragment()

    @ActivityScoped
    @Provides
    fun provideActionFragment() = ActionFragment()

    @ActivityScoped
    @Provides
    fun provideSettingsFragment() = SettingsFragment()

    @ActivityScoped
    @Provides
    fun provideHelpFragment() = HelpFragment()
}