package com.example.everybody_android.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        var token = ""
    }
}