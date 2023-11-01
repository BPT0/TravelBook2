package com.graduation.travelbook2.myDiary.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.databinding.ItemBookImgBinding
import com.graduation.travelbook2.myDiary.DiaryViewerActivity

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

    fun addImgDiary(mapImgDiary: MutableMap<Int, ArrayList<Uri>>){
        mapBook = mapImgDiary
    }

    inner class ImgViewHolder (private val binding: ItemBookImgBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(listImg: ArrayList<Uri>) {
            // 2.아이템뷰에 클릭리스너 붙이기
            itemView.setOnClickListener {
                // 넘겨줄 액티비티 : 뷰페이져에 book의 img들이 보여질 화면
                val intent = Intent(context, DiaryViewerActivity::class.java)
                // 넘겨줄 데이터 : book의 img들
                intent.putExtra("diaryImgs", listImg)
                ContextCompat.startActivity(context, intent, null)
            }
            // 1.uri로 가져온 표지 이미지 보여주기
            binding.apply {
                Glide.with(itemView)
                    .load(listImg[0])
                    .into(ivBookImg)
            }
        }
    }

}