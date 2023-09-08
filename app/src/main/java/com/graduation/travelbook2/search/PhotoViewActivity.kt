package com.graduation.travelbook2.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityPhotoViewBinding
import com.graduation.travelbook2.databinding.ActivitySortedByLocalBinding

class PhotoViewActivity : BaseActivity<ActivityPhotoViewBinding>() {
    override val TAG : String = PhotoViewActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_photo_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        val img = intent.getStringExtra("photoPath")

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