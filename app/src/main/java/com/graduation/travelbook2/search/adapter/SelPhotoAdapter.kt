package com.graduation.travelbook2.search.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduation.travelbook2.database.ImgInfo

class SelPhotoAdapter (val listSelPhoto: ArrayList<ImgInfo>)
    : RecyclerView.Adapter<SelPhotoAdapter.PhotoViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelPhotoAdapter.PhotoViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SelPhotoAdapter.PhotoViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class PhotoViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

}