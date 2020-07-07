package com.hamidjonhamidov.androidbasicsexamples.di

import androidx.lifecycle.ViewModelProvider
import com.hamidjonhamidov.androidbasicsexamples.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}