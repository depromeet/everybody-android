package com.example.everybody_android.pref

import android.content.Context
import android.content.SharedPreferences

class SharedPrefStorage(val context: Context) : LocalStorage {

    private val fcmTokenKey = "FcmToken"
    private val devicePrimaryKey = "DeviceToken"
    private val userId = "UserId"


    override fun saveFcmToken(token: String) =
        getPref(context).edit().let {
            it.putString(fcmTokenKey, token)
            it.apply()
        }

    override fun getFcmToken(): String = getPref(context).getString(fcmTokenKey, "").toString()


    override fun saveDeviceToken(token: String) =
        getPref(context).edit().let {
            it.putString(devicePrimaryKey, token)
            it.apply()
        }

    override fun getDeviceToken() = getPref(context).getString(fcmTokenKey, "").toString()

    override fun getUserId(): Int = getPref(context).getInt(userId, -1)

    override fun saveUserId(id: Int) {
        getPref(context).edit().let {
            it.putInt(userId, id)
            it.apply()
        }
    }

    private fun getPref(context: Context): SharedPreferences =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)

}