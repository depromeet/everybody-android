package com.example.everybody_android.dto.request

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("user_id")
    val userId : String,

    @SerializedName("password")
    val password : String
)
