package com.graduation.travelbook2.search.listener

import android.view.View
import com.graduation.travelbook2.database.ImgInfo

interface ItemIntentClickListener {
    fun onItemClickIntent(view: View, imgInfo:ImgInfo, pos: Int)
}