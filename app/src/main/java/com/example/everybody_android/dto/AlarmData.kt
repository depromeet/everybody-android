package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class AlarmData(

    val monday : String,

    val tuesday : String,

    val wednesday : String,

    val thursday : String,

    val friday : String,

    val saturday : String,

    val sunday : String,

    @SerializedName("preferred_time_hour")
    val preferredTimeHour : String,

    @SerializedName("preferred_time_minute")
    val preferredTimeMinute : String,

    @SerializedName("is_activated")
    val isActivated : Boolean
)
