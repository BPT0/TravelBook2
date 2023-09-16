package com.graduation.travelbook2.search.dto

import com.graduation.travelbook2.database.ImgInfo

data class SelectedImgDto(
    val imgIndex : Int,
    val imgInfo : ImgInfo,
)
