package com.graduation.travelbook2.search.modify

import android.os.Build
import android.os.Bundle
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivitySelFirstImgBinding

class SelFirstImgActivity(override val TAG: String, override val layoutRes: Int)
    : BaseActivity<ActivitySelFirstImgBinding>(){

    private lateinit var selectedImg: ArrayList<ImgInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("photoList", ImgInfo::class.java)!!
        }else{
            intent.getParcelableArrayListExtra<ImgInfo>("photoList")!!
        }

    }
}