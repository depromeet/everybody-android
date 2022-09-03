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
}