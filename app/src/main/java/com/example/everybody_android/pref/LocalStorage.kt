package com.example.everybody_android.pref

interface LocalStorage {

    fun saveFcmToken(token : String)

    fun getFcmToken() : String

    fun saveDeviceToken(token : String)

    fun getDeviceToken() : String
}