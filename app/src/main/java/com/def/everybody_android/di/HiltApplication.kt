package com.def.everybody_android.di

import android.app.Application
import android.util.Log
import com.def.everybody_android.dto.UserData
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    companion object {
        var token = ""
        var userData: UserData? = null
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "4cfb36b0d3bc2648d919b567153397ad")
    }
}