package com.graduation.travelbook2.search.listenerNcallback

import com.graduation.travelbook2.database.ImgInfo

interface ItemClickListener {
    fun  onCLickLocal(pos: Int, s: String)
    fun onClickImg(pos: Int, img: ImgInfo)
}