package com.example.everybody_android.data.response


import com.google.gson.annotations.SerializedName

data class UploadPictureResponse(
    @SerializedName("id")
    val id: Int? = null, // 53
    @SerializedName("album_id")
    val albumId: Int? = null, // 10
    @SerializedName("body_part")
    val bodyPart: String? = null, // upper
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/48/a8e08480-7be2-40d6-adc0-c8f07a2284b1?Expires=1636732347&Signature=kL1mIYN9-daHQH-h9tByW1yVQqA9dP5-aIZ286EbugRGqACYX5JjOXUMjwiQ4jS3nHbofbfvYlYrU9uinREJQcdsOTS7ayU-yP6ZxSGM5nFbu3h0BSXlEc58V7H4b2t7rgyDeLjy9bbK4ogc7Anxw1G3Ko~553tlwu1pm3ybBaEHAPH6ijhbYwnGS1UxOyGdmyU8SikEjycYdDMX780MEIoVWGd5ryujbnn5W2VldqkTo4qZp~sYXAnmXWXpvNVymEqY76a44-2LFxEhUdOuWfphM4rU3qP8vaRDR0X-KPsRTx6R3yPbGdb36PcTYhHlb3AohvsMhJWatxHwsqQ6ig__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("preview_url")
    val previewUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/192/a8e08480-7be2-40d6-adc0-c8f07a2284b1?Expires=1636732347&Signature=mIQQdyZUqreDhz-7XvKbgG9EwLhVjO8JCIG-rmDIdvAnv338SKawiqPaCnnpeSpaMD8ZypBDAWFh3akxJXjLXjYi9eAkHYV84BtQ~BOQ05S~k8io61c7Pudfk1Es0U6G2kAdPMtE37pPu29Wmi1njvQwFyxFsTFHMa-vy1UAYZglhqzCCsWFiRSZW7qiSRyOaCT2Y-chab7cfYmyBlVHB7cCMZa2OM-WfEg7svFk9fjOZ3Npc~VzMDsojIvukkkEKPq4WMmHEhQEd-xCY7oCe1o6uWCRTPoJOCRwWjmjv4WGE4-mQvB75VD0s5c7UMFLJndz38jmB92YkhV2xz3~-w__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("image_url")
    val imageUrl: String? = null, // https://d1kmcfw2fqy7s4.cloudfront.net/20/image/768/a8e08480-7be2-40d6-adc0-c8f07a2284b1?Expires=1636732347&Signature=YnZuTp9U5MGQMAxNCD-Cb82aGvJoGX58YBG7mARrGSfU2caTegYI3MDoDLeSD86-E-A6m-A8Q72Xqh-8VGGahgjJ77XMxoWGSdaa4Wf5KoHFZnS7TgHc2Zk4vI4ZKtcBn3c37RTZhyLg7X6ShloR66dRsOkz90frFjx8dyky73Wi4BIDT2C~e2jAcnsTB36qjwCUwdOE4iV5~MF59VDa3P1wpUJHhlAaO2k63JkfDiDNIgHAA4xT8eFrphrzmTxtEQK3htEqf8sx5QyhZIm5rYEd-af6IIR3nY6ioPbjNUj1rJbUzi5NbCMg2tAWObn47DiTqENB55gKPPRFxWalcQ__&Key-Pair-Id=APKAJQ7ER27KOWZFQB3A
    @SerializedName("key")
    val key: String? = null, // a8e08480-7be2-40d6-adc0-c8f07a2284b1
    @SerializedName("taken_at_year")
    val takenAtYear: Int? = null, // 2021
    @SerializedName("taken_at_month")
    val takenAtMonth: Int? = null, // 1
    @SerializedName("taken_at_day")
    val takenAtDay: Int? = null, // 1
    @SerializedName("created_at")
    val createdAt: String? = null // 2021-11-12T15:51:27.036667779Z
)