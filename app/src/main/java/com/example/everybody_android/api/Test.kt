package com.example.everybody_android.api

import com.example.everybody_android.data.response.Albums
import com.example.everybody_android.di.ApiModule
import retrofit2.http.PUT

class Test {

    interface TestApi {
        @PUT("/test")
        suspend fun putNotice(): Albums
    }

    companion object {
        //suspend fun ss(): Albums = ApiModule.provideApiService(TestApi::class.java).putNotice()
    }

}