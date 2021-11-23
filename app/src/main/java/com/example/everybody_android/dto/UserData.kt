package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class UserData(

    @SerializedName("id")
    val userId : String,

    @SerializedName("nickname")
    val nickName : String,

    @SerializedName("motto")
    val motto : String,

    @SerializedName("height")
    val height : Int,

    @SerializedName("weight")
    val weight : Int,

    @SerializedName("kind")
    val kind : String,

    @SerializedName("profile_image")
    val profileImage : String?,

    @SerializedName("created_at")
    val createdAt : String
)
