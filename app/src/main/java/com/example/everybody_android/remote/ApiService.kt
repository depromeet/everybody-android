package com.example.everybody_android.remote

import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.MainFeedData
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/albums")
    suspend fun getMainFeed(
    ) : List<MainFeedData>


    companion object{
        suspend fun getMainFeed() : List<MainFeedData> =
            ApiModule.provideApiService().getMainFeed()
    }
}