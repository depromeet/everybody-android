package com.example.everybody_android.pref

import android.content.Context
import android.content.SharedPreferences

class SharedPrefStorage(val context: Context) : LocalStorage {

    private val fcmTokenKey = "FcmToken"

    private val devicePrimaryKey = "DeviceToken"


    override fun saveFcmToken(token: String) =
        getPref(context).edit().let {
            it.putString(fcmTokenKey, token)
            it.apply()
        }

    override fun getFcmToken(): String =
        "Bearer " + getPref(context).getString(fcmTokenKey, "")


    override fun saveDeviceToken(token: String) =
        getPref(context).edit().let {
            it.putString(devicePrimaryKey, token)
            it.apply()
        }

    override fun getDeviceToken()  =
        "Bearer " + getPref(context).getString(fcmTokenKey, "")

    private fun getPref(context: Context): SharedPreferences =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)

}