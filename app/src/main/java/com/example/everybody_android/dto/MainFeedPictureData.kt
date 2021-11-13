package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class MainFeedPictureData(

    @SerializedName("id")
    val pictureId : Int,

    @SerializedName("album_id")
    val albumId : Int,

    @SerializedName("body_port")
    val bodyPart : String,

    @SerializedName("created_at")
    val pictureCreated : String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl : String,

    @SerializedName("preview_url")
    val previewUrl : String,

    @SerializedName("image_url")
    val ImageUrl : String
)
