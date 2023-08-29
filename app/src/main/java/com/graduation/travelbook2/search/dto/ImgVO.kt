package com.pipecodingclub.travelbook.search.dto

import java.io.Serializable
import kotlin.time.Duration

data class ImgVO(
    val imageUri: String?,
    var checked: Boolean,
    val gps: DoubleArray?, // 위치정보
    val localName: String?,
    val date: String?,
): Serializable
