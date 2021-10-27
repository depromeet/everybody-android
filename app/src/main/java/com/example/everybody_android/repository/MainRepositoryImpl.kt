package com.example.everybody_android.repository

import com.example.everybody_android.dto.MainFeedData
import com.example.everybody_android.remote.ApiService

class MainRepositoryImpl(apiService: ApiService) : MainRepository {
    override suspend fun getMainFeed(gateWay: String): List<MainFeedData> {
        TODO("Not yet implemented")
    }

}