package com.hamidjonhamidov.androidbasicsexamples.di

import androidx.lifecycle.ViewModel
import com.hamidjonhamidov.androidbasicsexamples.ui.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@InstallIn(ActivityComponent::class)
@Module
abstract class ViewModelModules{

    // ************************** VIEWMODELS ****************************
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}