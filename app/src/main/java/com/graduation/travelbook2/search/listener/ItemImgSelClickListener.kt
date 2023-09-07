package com.graduation.travelbook2.search.listener

import android.view.View
import com.graduation.travelbook2.database.ImgInfo

interface ItemImgSelClickListener {
    fun onItemCheck(isChecked: Boolean, imgInfo: ImgInfo)
}