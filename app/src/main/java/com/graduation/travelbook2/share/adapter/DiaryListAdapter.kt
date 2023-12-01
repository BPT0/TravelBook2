package com.graduation.travelbook2.share.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.graduation.travelbook2.R
import com.graduation.travelbook2.databinding.ItemBookImgBinding
import com.graduation.travelbook2.share.ViewerActivity
import org.jetbrains.anko.imageBitmap

class DiaryListAdapter(private val context: Context, private var mapBook: MutableMap<Int, ArrayList<Uri>>)
    : RecyclerView.Adapter<DiaryListAdapter.ImgViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryListAdapter.ImgViewHolder {
        val binding = ItemBookImgBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryListAdapter.ImgViewHolder, position: Int) {
        holder.bind(mapBook[position]!!)
    }

    override fun getItemCount(): Int = mapBook.size

    fun addImgDiary(diaryIndex: Int, diaryImgs: ArrayList<Uri>?){
        mapBook[diaryIndex] = diaryImgs!!
        Log.e("mapBook 아이템", mapBook[diaryIndex].toString())
    }

    inner class ImgViewHolder (private val binding: ItemBookImgBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(listImg: ArrayList<Uri>) {
            // 2.아이템뷰에 클릭리스너 붙이기
            itemView.setOnClickListener {
                // 넘겨줄 액티비티 : 뷰페이져에 book의 img들이 보여질 화면
                val intent = Intent(context, ViewerActivity::class.java)
                // 넘겨줄 데이터 : book의 img들
                intent.putExtra("diaryImgs", listImg)
                ContextCompat.startActivity(context, intent, null)
            }
            // 1.uri로 가져온 표지 이미지 보여주기
            binding.apply {
                Log.e("img", "$listImg")
                val firstItem = listImg.firstOrNull()
                if (firstItem != null) {
                    Glide.with(itemView)
                        .asBitmap()
                        .load(firstItem)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                ivBookImg.imageBitmap = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                        })
                } else {
                    Glide.with(itemView)
                        .asBitmap()
                        .load(R.drawable.img_add_info)
                        .into(ivBookImg)
                }


            }
        }
    }

}