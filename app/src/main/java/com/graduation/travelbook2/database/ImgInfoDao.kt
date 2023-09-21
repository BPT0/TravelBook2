package com.graduation.travelbook2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface ImgInfoDao {
    // 사진 정보 컬럼 1개 다이어리 생성
    @Insert
    fun insertImgInfo(imgInfo: ImgInfo)

    @Query("SELECT * FROM imginfo")
    fun getAllImgInfo(): List<ImgInfo>

    @Query("SELECT * FROM imgInfo WHERE locality = :localName")
    fun getLocalByImgInfo(localName: String): List<ImgInfo>

    // 지역별로 선택한 기간안의 날짜들의 img를 조회 (지역(for문 도는), 첫, 끝날짜 매개변수 필요)
    @Query("SELECT * FROM imgInfo WHERE locality = :localName AND date BETWEEN :startDate AND :endDate")
    fun getPeriodInLocalImg(localName: String, startDate: Int, endDate: Int): List<ImgInfo>
}