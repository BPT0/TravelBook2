package com.graduation.travelbook2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImgInfoDao {
    // 사진 정보 컬럼 1개 다이어리 생성
    @Insert
    fun insertImgInfo(imgInfo: ImgInfo)

    @Query("SELECT * FROM imginfo")
    fun getAllImgInfo(): List<ImgInfo>

    @Query("SELECT * FROM imgInfo WHERE locality = :localName")
    fun getLocalByImgInfo(localName: String): List<ImgInfo>
}