package com.graduation.travelbook2

import android.app.Application
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.sharedpref.PreferenceUtil

class MyApplication : Application(){
    var selectedImg1: ArrayList<SelectedImgDto> = ArrayList()
    companion object{
        lateinit var prefs: PreferenceUtil
    }
    override fun onCreate()
    {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}