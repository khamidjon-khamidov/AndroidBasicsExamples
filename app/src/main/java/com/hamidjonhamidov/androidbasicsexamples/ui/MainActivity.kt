package com.hamidjonhamidov.androidbasicsexamples.ui

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hamidjonhamidov.androidbasicsexamples.R
import com.hamidjonhamidov.androidbasicsexamples.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val mainViewModel: MainViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "my_test: ${mainViewModel.k}")

        startFragment()
    }

    fun startFragment() {
        val mFragment = MyFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_fragment, mFragment)
            .commit()
    }
}










