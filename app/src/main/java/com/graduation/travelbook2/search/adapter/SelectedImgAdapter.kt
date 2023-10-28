package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoSBinding
import com.graduation.travelbook2.internalDto.SelectedImgDto

class SelectedImgAdapter(val listSelectedPhoto: ArrayList<SelectedImgDto>)
    : RecyclerView.Adapter<SelectedImgAdapter.PhotoViewHolder>(){

    // 사진 체크 박스 리스너 변수 및 메서드 정의

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedImgAdapter.PhotoViewHolder {
        val binding = ItemPhotoSBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedImgAdapter.PhotoViewHolder, position: Int) {
        holder.bind(listSelectedPhoto[position].imgInfo!!)
    }

    fun deleteItem(position: Int){
        listSelectedPhoto.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSelectedPhoto.size)
    }


    override fun getItemCount(): Int = listSelectedPhoto.size

    inner class PhotoViewHolder(private val binding: ItemPhotoSBinding)
        : RecyclerView.ViewHolder(binding.root){
        // 상단RV클릭이 됬을때
        fun bind(photo: ImgInfo) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.path)
                    .into(ivImage)
                }
            }
    }
}

