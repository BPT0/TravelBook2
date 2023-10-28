package com.graduation.travelbook2.loading

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.graduation.travelbook2.R


class LoadingDialog(context: Context) : Dialog(context){

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)    // dialog 요부 요소 클릭 불가능

        val loadingImg = findViewById<ImageView>(R.id.iv_loading_icon)
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.color_animation)
        loadingImg.startAnimation(animation)
        // 배경 투명하게 바꿔줌
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}