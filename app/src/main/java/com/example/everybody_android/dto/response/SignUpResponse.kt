package com.example.everybody_android.dto.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

    @SerializedName("id")
    val userId : Int
)