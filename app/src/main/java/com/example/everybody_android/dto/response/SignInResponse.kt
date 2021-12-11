package com.example.everybody_android.dto.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("access_token")
    val accessToken: String
)