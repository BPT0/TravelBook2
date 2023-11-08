package com.graduation.travelbook2.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoSmallBinding
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemTouchHelperListener
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener


class ImgArrangeAdapter(var listSelectedImgs: ArrayList<SelectedImgDto>) :
    RecyclerView.Adapter<ImgArrangeAdapter.ImgViewHolder>(),
    ItemTouchHelperListener {

    lateinit var itemClickListener: ItemClickListener

    fun setClickListener(_listener : ItemClickListener){
        itemClickListener = _listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImgArrangeAdapter.ImgViewHolder {
        val binding = ItemPhotoSmallBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImgArrangeAdapter.ImgViewHolder, position: Int) {
        holder.bind(listSelectedImgs[position].imgInfo!!)
    }

    override fun getItemCount(): Int = listSelectedImgs.size

    inner class ImgViewHolder(private val binding: ItemPhotoSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnImg.setOnClickListener {
                itemClickListener.onClickImg(adapterPosition, listSelectedImgs[adapterPosition].imgInfo!!)
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

    override fun onItemMove(form_position: Int, to_position: Int): Boolean {
        val img: SelectedImgDto = listSelectedImgs[form_position]
        listSelectedImgs.removeAt(form_position)
        listSelectedImgs.add(to_position, img)
        img.imgIndex = to_position
        notifyItemMoved(form_position, to_position)
        return true
    }

    override fun onItemSwipe(position: Int) {
        listSelectedImgs.removeAt(position)
        notifyItemRemoved(position)
    }


}

