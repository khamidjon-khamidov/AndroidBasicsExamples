package com.hamidjonhamidov.androidbasicsexamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlin.random.Random

class MainViewModel
@Inject
constructor(): ViewModel() {

    val k = Random.nextInt()
}