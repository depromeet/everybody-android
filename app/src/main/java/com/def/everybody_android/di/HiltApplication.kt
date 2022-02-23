package com.def.everybody_android.di

import android.app.Application
import com.def.everybody_android.db.UserData
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        var token = ""
        var userData: UserData? = null
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}