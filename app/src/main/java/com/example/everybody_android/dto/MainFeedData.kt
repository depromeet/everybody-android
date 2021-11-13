package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class MainFeedData(

    @SerializedName("id")
    val feedId : Int,

    @SerializedName("name")
    val feedName : String,

    @SerializedName("created_at")
    val feedCreated : String,

    @SerializedName("picture")
    val feedPicture : MainFeedPictureData

)
