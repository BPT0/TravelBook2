package com.graduation.travelbook2.myDiary.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.R

class DiaryViewPagerAdapter (listImg: ArrayList<Uri>
) : RecyclerView.Adapter<DiaryViewPagerAdapter.PagerViewHolder>() {
    var item = listImg

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.item_book_fill, parent, false)){

        val ivDiary = itemView.findViewById<ImageView>(R.id.iv_book_img)

        fun bind(img: Uri){
            Glide.with(itemView)
                .load(img)
                .placeholder(R.drawable.icon_img_placeholder)
                .into(ivDiary)
        }
    }
}