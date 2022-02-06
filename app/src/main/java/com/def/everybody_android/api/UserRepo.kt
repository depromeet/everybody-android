package com.def.everybody_android.api

import com.def.everybody_android.di.ApiModule
import com.def.everybody_android.dto.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

class UserRepo {

    interface UserApi {
        @GET("/users/me")
        suspend fun getUserData(): UserData

        @PUT("/users/me")
        suspend fun putUserData(@Body mpa: Map<String, String>): Response<Unit>

        @POST("/feedbacks")
        suspend fun feedBack(@Body map: HashMap<String, Any>): Response<Unit>
    }

    companion object {
        suspend fun getUserData(): UserData =
            ApiModule.provideApiUser().getUserData()

        suspend fun putUserData(map: Map<String, String>): Response<Unit> =
            ApiModule.provideApiUser().putUserData(map)

        suspend fun feedBack(map: HashMap<String, Any>): Response<Unit> =
            ApiModule.provideApiUser().feedBack(map)
    }


}