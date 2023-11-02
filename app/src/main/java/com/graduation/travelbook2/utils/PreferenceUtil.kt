package com.graduation.travelbook2.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("other2", 0)

    fun getString(key: String, defValue: String): String
    {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String)
    {
        prefs.edit().putString(key, str).apply()
    }

    fun getBookIndex(key: String, defValue: Int): Int
    {
        return prefs.getInt(key, defValue)
    }

    fun setBookIndex(key: String, index: Int)
    {
        prefs.edit().putInt(key, index).apply()
    }

    fun getIsLoadImages(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setIsLoadImages(key: String, isLoaded: Boolean){
        prefs.edit().putBoolean(key, isLoaded)
    }


}