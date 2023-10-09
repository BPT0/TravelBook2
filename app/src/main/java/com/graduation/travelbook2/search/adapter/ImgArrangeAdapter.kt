package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoSBinding
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemTouchHelperListener


class ImgArrangeAdapter(var listSelectedImgs: ArrayList<SelectedImgDto>)
    : RecyclerView.Adapter<ImgArrangeAdapter.PhotoViewHolder>(),
        ItemTouchHelperListener{

        override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImgArrangeAdapter.PhotoViewHolder {
        val binding = ItemPhotoSBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImgArrangeAdapter.PhotoViewHolder, position: Int) {
        holder.bind(listSelectedImgs[position].imgInfo!!)
    }

    fun deleteItem(position: Int){
        listSelectedImgs.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSelectedImgs.size)
    }


    override fun getItemCount(): Int = listSelectedImgs.size

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

    fun setItems(imgList: ArrayList<SelectedImgDto>) {
        listSelectedImgs = imgList
        notifyDataSetChanged()
    }

    override fun onItemMove(form_position: Int, to_position: Int): Boolean {
        val img: SelectedImgDto = listSelectedImgs[form_position]
        listSelectedImgs.removeAt(form_position)
        listSelectedImgs.add(to_position, img)
        img.imgIndex = to_position
        notifyItemMoved(form_position, to_position)
        return true
    }

    override fun onItemSwipe(position: Int) {
        listSelectedImgs.removeAt(position);
        notifyItemRemoved(position);
    }


}

