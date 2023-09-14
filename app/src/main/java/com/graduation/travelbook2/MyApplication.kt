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

        /*val fOptions = FirebaseOptions.Builder()
            .setApiKey("AIzaSyBLth2tCfahl96kPmkdOk4Y46gn5VHwrow")
            .setApplicationId("1:187803405327:android:a92c8ffe86aab738afe77a")
            .setProjectId("travelbook2-2c9ea")
            .build()

        FirebaseApp.initializeApp(this, fOptions)*/
    }
}