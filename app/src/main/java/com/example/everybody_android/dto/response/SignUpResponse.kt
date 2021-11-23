package com.example.everybody_android.dto.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

    @SerializedName("user_id")
    val userId : Int,

    @SerializedName("password")
    val password : String
)