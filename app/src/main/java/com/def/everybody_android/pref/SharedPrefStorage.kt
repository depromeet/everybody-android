package com.def.everybody_android.pref

import android.content.Context
import android.content.SharedPreferences

class SharedPrefStorage(val context: Context) : LocalStorage {

    private val fcmTokenKey = "FcmToken"
    private val devicePrimaryKey = "DeviceToken"
    private val userId = "UserId"
    private val storage = "Storage"
    private val thumbnail = "thumbnail"
    private val authentication = "authentication"
    private val weight = "weight"
    private val weightVisible = "weightVisible"
    private val grid = "grid"
    private val pose = "pose"
    private val picturePart = "picturePart"
    private val timeSetting = "timeSetting"


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

    override fun isAppStorage(): Boolean = getPref(context).getBoolean(storage, false)

    override fun setAppStorage(isStorage: Boolean) {
        getPref(context).edit().let {
            it.putBoolean(storage, isStorage)
            it.apply()
        }
    }

    override fun isThumbnailBlind(): Boolean = getPref(context).getBoolean(thumbnail, false)

    override fun setThumbnailBlind(isThumbnailBlind: Boolean) {
        getPref(context).edit().let {
            it.putBoolean(thumbnail, isThumbnailBlind)
            it.apply()
        }
    }

    override fun isAuthentication(): Boolean = getPref(context).getBoolean(authentication, false)

    override fun setAuthentication(isAuthentication: Boolean) {
        getPref(context).edit().let {
            it.putBoolean(authentication, isAuthentication)
            it.apply()
        }
    }

    override fun setWeight(weight: String) {
        getPref(context).edit().let {
            it.putString(this.weight, weight)
            it.apply()
        }
    }

    override fun getWeight(): String = getPref(context).getString(weight, "50.3") ?: "50.3"

    override fun setWeightVisible(isVisible: Boolean) {
        getPref(context).edit().let {
            it.putBoolean(weightVisible, isVisible)
            it.apply()
        }
    }

    override fun isWeightVisible(): Boolean = getPref(context).getBoolean(weightVisible, false)
    override fun setGrid(isGrid: Boolean) {
        getPref(context).edit().let {
            it.putBoolean(grid, isGrid)
            it.apply()
        }
    }

    override fun isGrid(): Boolean = getPref(context).getBoolean(grid, false)

    override fun setPose(index: Int) {
        getPref(context).edit().let {
            it.putInt(pose, index)
            it.apply()
        }
    }

    override fun getPose(): Int = getPref(context).getInt(pose, 0)

    override fun setPicturePart(part: String) {
        getPref(context).edit().let {
            it.putString(picturePart, part)
            it.apply()
        }
    }

    override fun getPicturePart(): String = getPref(context).getString(picturePart, "whole") ?: "whole"

    override fun setTimeSetting(timeSetting: String) {
        getPref(context).edit().let {
            it.putString(this.timeSetting, timeSetting)
            it.apply()
        }
    }

    override fun getTimeSetting(): String = getPref(context).getString(timeSetting, "photoTime") ?: "photoTime"

    private fun getPref(context: Context): SharedPreferences =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)

}