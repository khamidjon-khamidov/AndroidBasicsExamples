package com.hamidjonhamidov.androidbasicsexamples

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(application: Application) {
    private val SHARED_PREF_KEY = "SHARED_PREF_KEY"
    private val SHARED_URI_KEY = "SHARED_PASSWORD_KEY"

    private val sharedPreferences: SharedPreferences by lazy {
        application.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
    }

    fun savePassword(password: String){
        with(sharedPreferences.edit()){
            putString(SHARED_URI_KEY, password)
            apply()
        }
    }

    fun getPassword(): String{
        return sharedPreferences.getString(SHARED_URI_KEY, "db_Khamidjon") ?: "db_Khamidjon"
    }
}