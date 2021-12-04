package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class AlarmData(

    @SerializedName("monday")
    val monday : Boolean,

    @SerializedName("tuesday")
    val tuesday : Boolean,

    @SerializedName("wednesday")
    val wednesday : Boolean,

    @SerializedName("thursday")
    val thursday : Boolean,

    @SerializedName("friday")
    val friday : Boolean,

    @SerializedName("saturday")
    val saturday : Boolean,

    @SerializedName("sunday")
    val sunday : Boolean,

    @SerializedName("preferred_time_hour")
    val preferredTimeHour : String,

    @SerializedName("preferred_time_minute")
    val preferredTimeMinute : String,

    @SerializedName("is_activated")
    val isActivated : Boolean
)