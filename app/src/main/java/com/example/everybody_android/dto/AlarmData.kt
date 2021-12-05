package com.example.everybody_android.dto

import com.google.gson.annotations.SerializedName

data class AlarmData(

    @SerializedName("monday")
    var monday : Boolean,

    @SerializedName("tuesday")
    var tuesday : Boolean,

    @SerializedName("wednesday")
    var wednesday : Boolean,

    @SerializedName("thursday")
    var thursday : Boolean,

    @SerializedName("friday")
    var friday : Boolean,

    @SerializedName("saturday")
    var saturday : Boolean,

    @SerializedName("sunday")
    var sunday : Boolean,

    @SerializedName("preferred_time_hour")
    var preferredTimeHour : Int,

    @SerializedName("preferred_time_minute")
    var preferredTimeMinute : Int,

    @SerializedName("is_activated")
    var isActivated : Boolean
)