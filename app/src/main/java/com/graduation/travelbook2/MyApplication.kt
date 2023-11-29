package com.graduation.travelbook2

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.utils.PreferenceUtil

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