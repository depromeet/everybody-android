package com.example.everybody_android.dto.response

import com.google.gson.annotations.SerializedName

data class CreateAlbumResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("description")
    val description: String
)