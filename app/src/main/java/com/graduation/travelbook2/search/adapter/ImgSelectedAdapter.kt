package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoSBinding
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener

class ImgSelectedAdapter (val listSelectedPhoto: ArrayList<SelectedImgDto>)
    : RecyclerView.Adapter<ImgSelectedAdapter.ImgViewHolder>(){

    lateinit var itemClickListener: ItemClickListener

    fun setClickListener(_listener : ItemClickListener){
        itemClickListener = _listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImgSelectedAdapter.ImgViewHolder {
        val binding = ItemPhotoSBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImgSelectedAdapter.ImgViewHolder, position: Int) {
        holder.bind(listSelectedPhoto[position].imgInfo!!)
    }


    override fun getItemCount(): Int = listSelectedPhoto.size

    inner class ImgViewHolder(private val binding: ItemPhotoSBinding)
        : RecyclerView.ViewHolder(binding.root){

        init {
            // 상단RV클릭이 됬을때
            binding.btnImg.setOnClickListener {
                itemClickListener.onClickImg(adapterPosition, listSelectedPhoto[adapterPosition].imgInfo!!)
            }
        }

        fun bind(photo: ImgInfo) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.path)
                    .into(ivImage)
            }
        }
    }
}

