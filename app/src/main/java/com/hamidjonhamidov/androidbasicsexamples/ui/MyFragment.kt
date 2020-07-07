package com.hamidjonhamidov.androidbasicsexamples.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.hamidjonhamidov.androidbasicsexamples.R
import com.hamidjonhamidov.androidbasicsexamples.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFragment : Fragment(R.layout.fragment_my) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: MainViewModel by activityViewModels {
        viewModelFactory
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyFragment", "my_test: ${viewModel.k}")
    }
}