package com.example.everybody_android.repository

import com.example.everybody_android.dto.MainFeedData

interface MainRepository {
    suspend fun getMainFeed(gateWay : String) : List<MainFeedData>
}