package com.graduation.travelbook2.search.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemLocalBinding
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener

class LocalAdapter(var listLocal: ArrayList<String>,
                   var mapSortedImgVO: HashMap<String, ArrayList<ImgInfo>>
    ) : RecyclerView.Adapter<LocalAdapter.LocalViewHolder>() {
    companion object {
        private const val TAG = "LocalAdapter"
    }

    lateinit var itemClickListener: ItemClickListener

    fun setListener(_listener : ItemClickListener){
        itemClickListener = _listener
    }

    // ViewHolder 생성하는 함수, 최소 생성 횟수만큼만 호출됨 (계속 호출 X)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val binding = ItemLocalBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        return LocalViewHolder(binding)
    }

    // 만들어진 ViewHolder에 데이터를 바인딩하는 함수
    // position = 리스트 상에서 몇번째인지 의미
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        holder.bind(listLocal[position], mapSortedImgVO[listLocal[position]]!!.random())
    }

    override fun getItemCount(): Int = listLocal.size

    fun changeLocalList(list: ArrayList<String>) {
        listLocal = list
    }

    inner class LocalViewHolder(private val binding: ItemLocalBinding)
        : RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.btnLocal.setOnClickListener {
                itemClickListener.onCLickLocal(adapterPosition, listLocal[adapterPosition])
            }
        }
        fun bind(localName: String, imgInfo: ImgInfo) {
            binding.apply {
                btnLocal.text = localName

                val imageUri = Uri.parse(imgInfo.path) // 이미지의 URI에 맞게 변경해주세요
                val imageDrawable = Drawable.createFromPath(imageUri.path)
                imageDrawable?.alpha = 150

                Glide.with(itemView)
                    .load(imageDrawable)
                    .into(object : CustomTarget<Drawable>(){
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            btnLocal.background = resource
                            btnLocal.backgroundTintList = null
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        }

    }

}