package com.def.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class MainFeedPictureData(

    @SerializedName("id")
    val pictureId : Int,

    @SerializedName("album_id")
    val albumId : Int,

    @SerializedName("body_port")
    val bodyPart : String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl : String,

    @SerializedName("preview_url")
    val previewUrl : String,

    @SerializedName("image_url")
    val imageUrl : String,

    @SerializedName("taken_at_day")
    val takenAtDay : String,

    @SerializedName("created_at")
    val pictureCreated : String,
)
