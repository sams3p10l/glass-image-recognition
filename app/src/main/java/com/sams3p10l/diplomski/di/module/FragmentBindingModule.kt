package com.sams3p10l.diplomski.di.module

import com.sams3p10l.diplomski.ui.fragment.ActionFragment
import com.sams3p10l.diplomski.ui.fragment.HelpFragment
import com.sams3p10l.diplomski.ui.fragment.HomeFragment
import com.sams3p10l.diplomski.ui.fragment.SettingsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object FragmentBindingModule {

    @Provides
    fun provideHomeFragment() = HomeFragment()

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