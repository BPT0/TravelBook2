package com.graduation.travelbook2.search

import android.os.Bundle
import com.bumptech.glide.Glide
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityImgFullBinding

class ImgFullActivity : BaseActivity<ActivityImgFullBinding>(){
    override val TAG : String = ImgFullActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_img_full
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val img = intent.getStringExtra("imgPath")!!

        binding.apply {
            ivFull.apply {
                Glide.with(context)
                    .load(img)
                    .into(this)

                setOnClickListener{
                    supportFinishAfterTransition()
                }
            }
        }
    }
}
