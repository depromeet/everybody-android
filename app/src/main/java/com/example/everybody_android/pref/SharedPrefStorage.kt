package com.example.everybody_android.pref

import android.content.Context
import android.content.SharedPreferences

class SharedPrefStorage(val context: Context) : LocalStorage {

    private val tokenKey = "Token"


    override fun saveToken(token: String) =
        getPref(context).edit().let {
            it.putString(tokenKey, token)
            it.apply()
        }

    override fun getToken(token: String) {
        TODO("Not yet implemented")
    }

    private fun getPref(context: Context): SharedPreferences =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)

}