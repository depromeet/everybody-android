package com.example.everybody_android.api

import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.UserData
import retrofit2.http.GET
import retrofit2.http.Header

class UserRepo {

    interface UserApi {
        @GET("/users/me")
        suspend fun getUserData() : UserData
    }

    companion object{
        suspend fun getUserData() : UserData =
            ApiModule.provideApiUser().getUserData()
    }


}