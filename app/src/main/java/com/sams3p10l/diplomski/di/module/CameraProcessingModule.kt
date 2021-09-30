package com.sams3p10l.diplomski.di.module

import com.sams3p10l.diplomski.processing.TextProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object CameraProcessingModule {

    @Provides
    fun provideTextProcessor() = TextProcessor()
}