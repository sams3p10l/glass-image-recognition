package com.sams3p10l.diplomski.di.module

import android.content.Context
import android.view.SurfaceView
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class CameraModule {

    @FragmentScoped
    @Provides
    fun provideSurfaceView(@ApplicationContext context: Context) = SurfaceView(context)

    @FragmentScoped
    @Provides
    fun provideTextRecognizer() = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
}