/*
package com.pipecodingclub.travelbook.search.adapter

import com.pipecodingclub.travelbook.search.dto.ImgVO
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pipecodingclub.travelbook.databinding.ItemPhotoHBinding

class SelAlbumAdapter(var imageList: ArrayList<ImgVO>)
    : RecyclerView.Adapter<SelAlbumAdapter.ImageVOViewHolder>() {
    companion object {
        private const val TAG = "AlbumAdater"
    }

    // ViewHolder 생성하는 함수, 최소 생성 횟수만큼만 호출됨 (계속 호출 X)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVOViewHolder {
        val binding = ItemPhotoHBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        // 부모(리싸이클러뷰 = 뷰그룹), 리싸이클러뷰가 attach 하도록 해야함 (우리가 하면 안됨)

        return ImageVOViewHolder(binding)
    }

    // 만들어진 ViewHolder에 데이터를 바인딩하는 함수
    // position = 리스트 상에서 몇번째인지 의미
    override fun onBindViewHolder(holder: ImageVOViewHolder, position: Int) {
        holder.bind(imageList[position])

    }

    fun addImage(selImgList: ArrayList<ImgVO>){
        imageList = selImgList
        imageList.removeIf {
            it.imageUri == ""
        }
        notifyDataSetChanged()
    }

    fun delImage(selImgList: ArrayList<ImgVO>){
        imageList = selImgList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imageList.size

    class ImageVOViewHolder(private val binding: ItemPhotoHBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivImage
        val tvNoImg = binding.tvNoImg
        fun bind(image: ImgVO) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(image.imageUri)
                    .into(ivImage)

            }
        }
    }
}*/
