package com.example.everybody_android.data.response


import com.example.everybody_android.data.response.base.Picture
import com.google.gson.annotations.SerializedName

class AlbumsResponse : ArrayList<AlbumsResponse.Album>() {
    data class Album(
        @SerializedName("id")
        val id: Int, // 10
        @SerializedName("name")
        val name: String? = null, // 아요미의 두 번째 앨범!
        @SerializedName("created_at")
        val createdAt: String? = null, // 2021-11-02T13:54:29+09:00
        @SerializedName("pictures")
        val pictures: Pictures,
        @SerializedName("description")
        val description: String? = null // 11일 간의 기록
    ) {
        data class Pictures(
            @SerializedName("upper")
            val upper: List<Picture>? = null,
            @SerializedName("lower")
            val lower: List<Picture>? = null,
            @SerializedName("whole")
            val whole: List<Picture>? = null
        )
    }
}