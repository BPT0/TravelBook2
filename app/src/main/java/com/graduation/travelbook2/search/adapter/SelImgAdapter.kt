package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoLBinding
import com.pipecodingclub.travelbook.search.listener.ItemLocalClickListener

class SelImgAdapter (val listLocalPhoto: ArrayList<ImgInfo>)
    : RecyclerView.Adapter<SelImgAdapter.PhotoViewHolder>(){

    // todo.
    //  1. 클릭리스너 정의
    // lateinit var itemselClickListener: OnItemSelClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelImgAdapter.PhotoViewHolder {
        val binding = ItemPhotoLBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelImgAdapter.PhotoViewHolder, position: Int) {
        holder.bind(listLocalPhoto[position])
    }

    override fun getItemCount(): Int = listLocalPhoto.size

    inner class PhotoViewHolder(private val binding: ItemPhotoLBinding)
        : RecyclerView.ViewHolder(binding.root){
        init {

        }
        fun bind(localPhoto: ImgInfo) {
            binding.apply {
                // todo
                //  1. 이미지 클릭시(전체 범위) 체크박스 선택 및 해제
                //  1-1. 체크박스 선택시 이미지 하단 sleectedRV에 표시

                // 2. 사진 표시
                Glide.with(itemView)
                    .load(localPhoto.path)
                    .into(ivImage)
            }
        }
    }

}