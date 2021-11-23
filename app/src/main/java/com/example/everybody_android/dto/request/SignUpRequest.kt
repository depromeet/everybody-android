package com.example.everybody_android.dto.request

import com.example.everybody_android.dto.AlarmData
import com.example.everybody_android.dto.DeviceTokenData
import com.google.gson.annotations.SerializedName

data class SignUpRequest(

    val password : String,

    val height : Int,

    val weight : Int,

    @SerializedName("notification_config")
    val notificationConfig : AlarmData,

    val device : DeviceTokenData,

    val kind : String

)
