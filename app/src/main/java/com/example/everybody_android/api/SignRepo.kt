package com.example.everybody_android.api

import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.request.SignInRequest
import com.example.everybody_android.dto.request.SignUpRequest
import com.example.everybody_android.dto.response.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

class SignRepo {

    interface SignApi{
        @POST("/auth/signup")
        suspend fun signUp(
            @Body signUpRequest: SignUpRequest
        ) : SignUpResponse

        @POST("/auth/login")
        suspend fun signIn(
            @Body signInRequest: SignInRequest
        ) : String
    }

    companion object{
        suspend fun signUp(signUpRequest: SignUpRequest) : SignUpResponse =
            ApiModule.provideApiSign().signUp(signUpRequest)

        suspend fun signIn(signInRequest: SignInRequest) : String =
            ApiModule.provideApiSign().signIn(signInRequest)
    }
}