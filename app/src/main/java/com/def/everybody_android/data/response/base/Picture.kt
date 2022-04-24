package com.def.everybody_android.data.response.base

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Picture(
    @SerializedName("id")
    val id: Int? = null, // 40
    @SerializedName("album_id")
    val albumId: Int? = null, // 10
    @SerializedName("body_part")
    val bodyPart: String, // upper
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/48/366934a4-e98a-439a-9fed-b339d55d04eb?Expires=1636726975&Signature=lQ7mR6Va9xhc3BrR6gsXWIZiFUZ1XxqNEnFhzKFluiXjnl4J0jILssSexA3dWjH8Alj6XTvpB0wuAjdUvKbmn0Nf6sfcOO5rhxFlOyxp9l21JEjMJZ5-wR-ANoVK4~kMo-hcDX5KCBEO0BdV8vJjeTXb2FYGeC-kbETV~gW2ThkhgtuJc1QQhy2cqA-UqBBXJ8kh8RlLZq1aaZBIHsmBFod1E7BQc66BHrLrSNfsr1tGewSr9pi~e-fmwc1wdPQktOGk1b6o5by0svt9jmoow9BO1nAkQRw3JEuTdWJ24i3TL7SQWzpT95rGG~rQkx6L5r6CFwrMMOcJ8U7-AQdCYg__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("preview_url")
    val previewUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/192/366934a4-e98a-439a-9fed-b339d55d04eb?Expires=1636726975&Signature=FWhrreMsvcCBHB1T1JKjIVgM5XRnbqbS5MSV2RUiQqTVqZUyc4aFM0J0oPD6iBNQyiKTCOssxjnSQ9qo-Om6qDQcMdzhn4rJ2I~7aCGcnMHehu4hyyBkrKTTcboijneH0uMx~XeKfq5hQdYwfXbOto8sbOET1XAMOeQtx7gPXeQB4sBM17Xvb1FF73H0LF8KqxgGH7uQxyiqTUJqieIHM-taPgRqLsys4jRfQ7p24rNRsI~fAcls-QKy5uNaQYcvOPPI3bCMMgYR2q507vIJaKMlzHJTExEOwnW5YRbkvWKFa94E6kXeFY41VEkBHe0fsp2~BuppCP1mporIa84PEQ__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("image_url")
    val imageUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/768/366934a4-e98a-439a-9fed-b339d55d04eb?Expires=1636726975&Signature=cF9Ta20ooTUdSsPBaeaq9uDj2uR~nhj2QS3GlUhpbydRYyqOCOlBLyOdf~6Rf-oROwJ8jPxJDqHcq34FMhHv7K7~BIrTcrp1aZf7w2TOFG~7-w4rSm7hRFdgLNBoMuZsu1uLmJEiXbf2wTsSaRaBeQVxsjVYHZo6sEKC5AcLQ-~Jj4pIKcAZ5gqL1lO3Z4pinET0HCKx9MMArXwVOZagSETqrqSqyZzIoKyp~ODLnoOBFfaJCjLHP8-b8xF~hC4VLRVnmwXL3eEsSj-j8c7RGMZZ~YRokHVjm7j6o8RHMwBLVJYu6RaKrsjc0r2qFm0K1KGRScIpb6hE~K0wQo0xaQ__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("key")
    val key: String? = null, // 366934a4-e98a-439a-9fed-b339d55d04eb
    @SerializedName("taken_at_year")
    val takenAtYear: Int? = null, // 1
    @SerializedName("taken_at_month")
    val takenAtMonth: Int? = null, // 1
    @SerializedName("taken_at_day")
    val takenAtDay: Int? = null, // 1
    @SerializedName("created_at")
    val createdAt: String? = null // 2021-11-02T14:12:18+09:00
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(albumId)
        parcel.writeString(bodyPart)
        parcel.writeString(thumbnailUrl)
        parcel.writeString(previewUrl)
        parcel.writeString(imageUrl)
        parcel.writeString(key)
        parcel.writeValue(takenAtYear)
        parcel.writeValue(takenAtMonth)
        parcel.writeValue(takenAtDay)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Picture> {
        override fun createFromParcel(parcel: Parcel): Picture {
            return Picture(parcel)
        }

        override fun newArray(size: Int): Array<Picture?> {
            return arrayOfNulls(size)
        }
    }
}
