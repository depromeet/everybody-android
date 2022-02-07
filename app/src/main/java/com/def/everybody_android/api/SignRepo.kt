package com.def.everybody_android.api

import com.def.everybody_android.di.ApiModule
import com.def.everybody_android.dto.request.SignInRequest
import com.def.everybody_android.dto.request.SignUpRequest
import com.def.everybody_android.dto.response.SignInResponse
import com.def.everybody_android.dto.response.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

class SignRepo {

    interface SignApi {
        @POST("/auth/signup")
        suspend fun signUp(
            @Body signUpRequest: SignUpRequest
        ): SignUpResponse

        @POST("/auth/login")
        suspend fun signIn(
            @Body signInRequest: SignInRequest
        ): SignInResponse

        @POST("/oauth/login")
        suspend fun oauthLogin(@Body map: HashMap<String, Any>): SignInResponse
    }

    companion object {
        suspend fun signUp(signUpRequest: SignUpRequest): SignUpResponse =
            ApiModule.provideApiSign().signUp(signUpRequest)

        suspend fun signIn(signInRequest: SignInRequest): SignInResponse =
            ApiModule.provideApiSign().signIn(signInRequest)

        suspend fun oauthLogin(map: HashMap<String, Any>): SignInResponse =
            ApiModule.provideApiSign().oauthLogin(map)
    }
}