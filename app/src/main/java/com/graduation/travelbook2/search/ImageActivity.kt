/*
package com.pipecodingclub.travelbook.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.pipecodingclub.travelbook.databinding.ActivityImageFullBinding
import com.pipecodingclub.travelbook.search.dto.ImgVO

class ImageActivity : AppCompatActivity() {
    private var imgFBinding: ActivityImageFullBinding? = null
    val binding get() = imgFBinding!!

    companion object{

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgFBinding = ActivityImageFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val img = intent.getSerializableExtra("clickImage") as ImgVO

        binding.apply {
            ivFull.apply {
                Glide.with(context)
                    .load(img.imageUri)
                    .into(this)
                setOnClickListener{
                    supportFinishAfterTransition()
                }
            }
        }
    }
}*/
