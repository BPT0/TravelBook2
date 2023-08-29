package com.pipecodingclub.travelbook.search.dto

import com.pipecodingclub.travelbook.search.dto.ImgVO
import java.io.Serializable


data class LocalByImageVO(
    val localByImgVo:  HashMap<String, ArrayList<ImgVO>>,
):Serializable
