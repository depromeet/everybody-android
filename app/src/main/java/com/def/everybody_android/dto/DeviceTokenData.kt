package com.def.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class DeviceTokenData(

    @SerializedName("device_token")
    val deviceToken : String,

    @SerializedName("push_token")
    val pushToken : String,

    @SerializedName("device_os")
    val deviceOs : String
)
