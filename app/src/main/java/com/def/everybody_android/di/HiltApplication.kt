package com.def.everybody_android.di

import android.app.Application
import com.def.everybody_android.dto.UserData
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        var token = ""
        var userData: UserData? = null
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config: RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true) // UI Thread에서도 realm에 접근할 수 있도록 한다.
            .build()

        Realm.setDefaultConfiguration(config)
    }
}