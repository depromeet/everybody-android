package com.def.everybody_android.dto.request


import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("device")
    val device: Device,
    @SerializedName("height")
    val height: Int? = null, // 165
    @SerializedName("kind")
    val kind: String, // SIMPLE
    @SerializedName("notification_config")
    val notificationConfig: NotificationConfig? = null,
    @SerializedName("password")
    val password: String, // 123123
    @SerializedName("weight")
    val weight: Int? = null // 55
) {
    data class Device(
        @SerializedName("device_os")
        val deviceOs: String, // IOS
        @SerializedName("device_token")
        val deviceToken: String, // some ios token he he he
        @SerializedName("push_token")
        val pushToken: String // fmjdpOMzQfaJCZkMCnxXyd:APA91bEMmKsdyXLRTknsivGTQ-MHfFX-Abf8z9D1WCMi5wpVJZi98ZFXEJGW41UkD4uQc_Uzg1r6_jdAYuekkqQyoC1IsSaijLAcxQsKdJND1lOkinPIYxWAMI24t5nFqNmKDJRKwp7P
    )

    data class NotificationConfig(
        @SerializedName("friday")
        val friday: Boolean, // true
        @SerializedName("is_activated")
        val isActivated: Boolean, // true
        @SerializedName("monday")
        val monday: Boolean, // true
        @SerializedName("preferred_time_hour")
        val preferredTimeHour: Int, // 20
        @SerializedName("preferred_time_minute")
        val preferredTimeMinute: Int, // 0
        @SerializedName("saturday")
        val saturday: Boolean, // false
        @SerializedName("sunday")
        val sunday: Boolean, // false
        @SerializedName("thursday")
        val thursday: Boolean, // true
        @SerializedName("tuesday")
        val tuesday: Boolean, // true
        @SerializedName("wednesday")
        val wednesday: Boolean // true
    )
}