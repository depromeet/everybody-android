package com.example.everybody_android.pref

interface LocalStorage {

    fun saveToken(token : String)

    fun getToken(token: String)
}