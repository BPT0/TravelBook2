package com.graduation.travelbook2.repo

import androidx.lifecycle.MutableLiveData
import com.graduation.travelbook2.externalDto.ImgDto

class PersnoalBookRepo {
    var listBook = MutableLiveData<ImgDto>()
    fun getBookFirstImgs(){

    }

    var book = MutableLiveData<ImgDto>()
}