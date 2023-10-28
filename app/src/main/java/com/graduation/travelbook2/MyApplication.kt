package com.graduation.travelbook2

import android.app.Application
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.utils.PreferenceUtil

class MyApplication : Application(){
    var selectedImg1: ArrayList<SelectedImgDto> = ArrayList()
    var bookIndex: Int = 0

    companion object{
        lateinit var prefs: PreferenceUtil
    }
    override fun onCreate()
    {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}