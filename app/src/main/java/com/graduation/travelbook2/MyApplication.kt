package com.graduation.travelbook2

import android.app.Application
import com.graduation.travelbook2.sharedpref.PreferenceUtil

class MyApplication : Application(){
    companion object{
        lateinit var prefs: PreferenceUtil
    }
    override fun onCreate()
    {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}