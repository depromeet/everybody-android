package com.example.everybody_android.remote

import com.example.everybody_android.dto.MainFeedData
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("{gateway_root}/albums")
    suspend fun getMainFeed(
        @Path("gateway_root") gateWay: String
    ) : List<MainFeedData>

}