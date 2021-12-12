package com.example.everybody_android.api

import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

class UserRepo {

    interface UserApi {
        @GET("/users/me")
        suspend fun getUserData(): UserData

        @PUT("/users/me")
        suspend fun putUserData(@Body mpa: Map<String, String>): Response<Unit>
    }

    companion object {
        suspend fun getUserData(): UserData =
            ApiModule.provideApiUser().getUserData()

        suspend fun putUserData(map: Map<String, String>): Response<Unit> =
            ApiModule.provideApiUser().putUserData(map)
    }


}