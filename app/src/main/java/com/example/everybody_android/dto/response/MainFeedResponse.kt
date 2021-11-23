package com.example.everybody_android.dto.response

import com.example.everybody_android.dto.MainFeedPicturePositionData
import com.google.gson.annotations.SerializedName

data class MainFeedResponse(

    @SerializedName("id")
    val id : Int,

    @SerializedName("name")
    val name : String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl : String,

    @SerializedName("created_at")
    val feedCreated : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("pictures")
    val feedPicture : MainFeedPicturePositionData

)
