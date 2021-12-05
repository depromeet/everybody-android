package com.example.everybody_android.data.response


import com.example.everybody_android.data.response.base.Picture
import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("id")
    val id: Int? = null, // 25
    @SerializedName("name")
    val name: String? = null, // 25번 앨범임돠!
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/40/image/48/45a0b1b7-de99-4109-9b5b-3947e1d7527d?Expires=1636823203&Signature=MaKDY~RQ~F46PiHWfxFvORnY1mhEMtZH7q3wYatgMh5cT96rZmuBa7gZUXFbtpZmG9fSswXMWbISq7xoAvgI-w3dasj7zCLA0BQBCpeKuhjfnd76r56-QmU8T3e8N1B9p6pvd6Ulf0nT0r9AsDtNm4wCCf00xxlaKnBJfPdCMhcZkFWm~H3CRQtidb7ZQbt9jgS3YmCvzSweOGtz8xJ3Rz5jSMlKaUu6dvcih31DXLrj6PGMVEa1lsvHHQHUMdyFdvc9LEJ7ns3PNibaMhFG9jnBoUEehNgf~25ZU2UY~UJR1cP-q7RaTVRoF9-jDHttiaj1zkwQ-qFBzRQwWjhaBQ__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("created_at")
    val createdAt: String? = null, // 2021-11-13T21:03:33+09:00
    @SerializedName("description")
    val description: String? = null, // 1일 간의 기록
    @SerializedName("pictures")
    val pictures: Pictures? = null
) {
    data class Pictures(
        @SerializedName("lower")
        val lower: List<Picture>? = null,
        @SerializedName("upper")
        val upper: List<Picture>? = null,
        @SerializedName("whole")
        val whole: List<Picture>? = null
    )
}