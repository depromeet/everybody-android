package com.example.everybody_android.api

import com.example.everybody_android.data.response.AlbumsResponse
import retrofit2.http.PUT

class Test {

    interface TestApi {
        @PUT("/test")
        suspend fun putNotice(): AlbumsResponse
    }

    companion object {
    }

}