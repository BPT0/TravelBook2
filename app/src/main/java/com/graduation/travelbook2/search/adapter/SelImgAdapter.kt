package com.graduation.travelbook2.search.adapter

import android.content.Intent
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoLBinding
import com.graduation.travelbook2.search.listener.ItemImgSelClickListener
import com.graduation.travelbook2.search.listener.ItemIntentClickListener

class SelImgAdapter (val listLocalPhoto: ArrayList<ImgInfo>)
    : RecyclerView.Adapter<SelImgAdapter.PhotoViewHolder>(){

    // 사진 체크 박스 리스너 변수 및 메서드 정의
    private lateinit var sitemSelClickListener: ItemImgSelClickListener
    fun setOnItemCheckedListener(itemSelClickListener: ItemImgSelClickListener){
        sitemSelClickListener = itemSelClickListener
    }
    // 체크 박스 상태를 저장하는 배열
    var chkBoxStatus : SparseBooleanArray = SparseBooleanArray()

    // 사진 클릭시 작동되는 리스너 변수 및 메서드 정의
    private lateinit var iOnItemClickListener: ItemIntentClickListener
    fun setOnItemIntentClickListener(_itemSelClickListener: ItemIntentClickListener){
        iOnItemClickListener = _itemSelClickListener
    }
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
        chkBoxStatus.put(itemCount - 1, true)
    }

    override fun getItemCount(): Int = listLocalPhoto.size

    inner class PhotoViewHolder(private val binding: ItemPhotoLBinding)
        : RecyclerView.ViewHolder(binding.root){
        val ckbSelImg = binding.cbSelImage
        init {
            /*ckbSelImg.setOnClickListener {
                // 체크박스 클릭 했을 때 처리
                ckbSelImg.setOnClickListener {
                    if(!ckbSelImg.isChecked) {
                        // 현재 아이템의 checked status 변경
                        chkBoxStatus.put(adapterPosition, false)
                    }
                    else {
                        // 현재 아이템의 checked status 변경
                        chkBoxStatus.put(adapterPosition, true)
                    }

                    // 추천 레시피 리스트 로드 처리를 위해 액티비티의 리프레시 매소드를 실행하며
                    // 이 메소드에 어답터에서 사용되는 정보를 넘김
                    sitemSelClickListener.onItemCheck(chkBoxStatus.get(adapterPosition), listLocalPhoto[adapterPosition])

                    // 리스트 새로고침 (CHECKED UI 갱신)

                }
            }*/

            binding.ivImage.setOnClickListener {
                iOnItemClickListener.onItemClickIntent(itemView, listLocalPhoto[adapterPosition],
                    adapterPosition)
            }
        }
        fun bind(localPhoto: ImgInfo) {
            binding.apply {
                Glide.with(itemView)
                    .load(localPhoto.path)
                    .into(ivImage)
            }
        }
    }

}