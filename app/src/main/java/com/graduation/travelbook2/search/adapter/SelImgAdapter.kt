package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemLocalBinding

class SelImgAdapter (val listLocalPhoto: ArrayList<ImgInfo>)
    : RecyclerView.Adapter<SelImgAdapter.PhotoViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelImgAdapter.PhotoViewHolder {
        val binding = ItemLocalBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelImgAdapter.PhotoViewHolder, position: Int) {
        holder.bind(listLocalPhoto[position])
    }

    override fun getItemCount(): Int = listLocalPhoto.size

    inner class PhotoViewHolder(private val binding: ItemLocalBinding)
        : RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnLocal.setOnClickListener {
                // todo: 리스너 정의
            }
        }
        fun bind(localPhoto: ImgInfo) {
            binding.apply {
                // todo: 체크박스 처리
            }
        }
    }

}