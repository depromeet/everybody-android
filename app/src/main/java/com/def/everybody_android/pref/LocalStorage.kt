package com.def.everybody_android.pref

interface LocalStorage {

    fun saveFcmToken(token: String)

    fun getFcmToken(): String

    fun saveDeviceToken(token: String)

    fun getDeviceToken(): String

    fun getUserId(): Int

    fun saveUserId(id: Int)

    fun isAppStorage(): Boolean

    fun setAppStorage(isStorage: Boolean)

    fun isThumbnailBlind(): Boolean

    fun setThumbnailBlind(isThumbnailBlind: Boolean)

    fun isAuthentication(): Boolean

    fun setAuthentication(isAuthentication: Boolean)

    fun setWeight(weight: String)

    fun getWeight(): String

    fun setWeightVisible(isVisible: Boolean)

    fun isWeightVisible(): Boolean

    fun setGrid(isGrid: Boolean)

    fun isGrid(): Boolean

    fun setPose(index: Int)

    fun getPose(): Int

    fun setPicturePart(part: String)

    fun getPicturePart(): String

    fun setTimeSetting(timeSetting: String)

    fun getTimeSetting(): String
}