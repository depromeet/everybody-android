package com.example.everybody_android.di

import android.app.Application
import com.example.everybody_android.dto.UserData
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        var token = ""
        var userData : UserData? = null
    }
}