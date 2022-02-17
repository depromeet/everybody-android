package com.def.everybody_android.di

import android.app.Application
import android.util.Log
import com.def.everybody_android.dto.UserData
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
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