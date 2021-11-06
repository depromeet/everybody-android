package com.example.everybody_android.data.response


import com.google.gson.annotations.SerializedName

class Albums : ArrayList<Albums.Album>() {
    data class Album(
        @SerializedName("created_at")
        val createdAt: String, // 2021-10-10T15:06:30Z
        @SerializedName("id")
        val id: Int, // 3
        @SerializedName("name")
        val name: String, // 3번째 앨범
        @SerializedName("pictures")
        val pictures: List<Picture>
    ) {
        data class Picture(
            @SerializedName("album_id")
            val albumId: Int, // 3
            @SerializedName("body_part")
            val bodyPart: String, // whole
            @SerializedName("created_at")
            val createdAt: String, // 2021-10-14T14:29:36Z
            @SerializedName("id")
            val id: Int, // 18
            @SerializedName("image_url")
            val imageUrl: String, // https://some.cloudfront.net/1/image/768/signature=ABCDEFGH&Key-Pair-Id=IJKLMNOP
            @SerializedName("preview_url")
            val previewUrl: String, // https://some.cloudfront.net/1/image/192/signature=ABCDEFGH&Key-Pair-Id=IJKLMNOP
            @SerializedName("thumbnail_url")
            val thumbnailUrl: String // https://some.cloudfront.net/1/image/48/signature=ABCDEFGH&Key-Pair-Id=IJKLMNOP
        )
    }
}