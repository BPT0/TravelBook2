package com.graduation.travelbook2.search.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ItemPhotoLBinding
import com.graduation.travelbook2.search.listener.ItemImgSelClickListener
import com.graduation.travelbook2.search.listener.ItemIntentClickListener

// recyclerview 체크박스 이벤트 처리
// https://velog.io/@keepcalm/ListView%EC%99%80-RecyclerView%EC%97%90%EC%84%9C-%EC%8A%A4%ED%81%AC%EB%A1%A4-%EC%8B%9C-checkbox-switch-%EC%84%A4%EC%A0%95-%ED%95%B4%EC%A0%9C%EB%90%98%EB%8A%94-%ED%98%84%EC%83%81-%EC%9B%90%EC%9D%B8%EA%B3%BC-%ED%95%B4%EA%B2%B0
class SelImgAdapter (var listLocalPhoto: ArrayList<ImgInfo>)
    : RecyclerView.Adapter<SelImgAdapter.PhotoViewHolder>(){

    // 사진 체크 박스 리스너 변수 및 메서드 정의
    private lateinit var sitemSelClickListener: ItemImgSelClickListener
    fun setOnItemCheckedListener(itemSelClickListener: ItemImgSelClickListener){
        sitemSelClickListener = itemSelClickListener
    }

    private val ckbStatus = SparseBooleanArray()

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

    fun changeImgList(listImg : ArrayList<ImgInfo>){
        listLocalPhoto = listImg
    }

    override fun onBindViewHolder(holder: SelImgAdapter.PhotoViewHolder, position: Int) {
        // data class 활용시: 체크 박스 상테 배열 요소를 먼저 추가해 주어야함
        // imgCheckBoxStatus.add(ImgCheckStatus(listLocalPhoto.size -1, false))
        holder.bind(listLocalPhoto[position])
    }

    // todo: 이미지 아이템들을 refresh하는 메서드 정의

    override fun getItemCount(): Int = listLocalPhoto.size

    inner class PhotoViewHolder(private val binding: ItemPhotoLBinding)
        : RecyclerView.ViewHolder(binding.root){
        init {
            binding.ivImage.setOnClickListener {
                iOnItemClickListener.onItemClickIntent(itemView, listLocalPhoto[adapterPosition],
                    adapterPosition)
            }
            binding.cbSelImage.setOnClickListener {

            }
        }
        fun bind(localPhoto: ImgInfo) {
            binding.apply {
                Glide.with(itemView)
                    .load(localPhoto.path)
                    .into(ivImage)

                /*cbSelImage.isChecked = imgStatus.isChecked

                cbSelImage.setOnClickListener {
                    imgStatus.isChecked = cbSelImage.isChecked
                    notifyItemChanged(adapterPosition)
                }*/
            }

            binding.cbSelImage.apply{
                isChecked = ckbStatus[adapterPosition]
                setOnClickListener {
                    // 체크박스 클릭 했을 때 처리
                    if(!isChecked) {
                        // 현재 아이템의 checked status 변경
                        ckbStatus.put(adapterPosition, false)
                    }
                    else {
                        // 현재 아이템의 checked status 변경
                        ckbStatus.put(adapterPosition, true)
                    }

                    // 하단의 선택된 사진을 표시하는 리싸이클러뷰에서 사진을 보여주기 위해
                    // 엑티비티에 어답터가 사용되는 정보를 넘김
                    sitemSelClickListener.onItemCheck(ckbStatus.get(adapterPosition),
                        listLocalPhoto[adapterPosition], adapterPosition)
                    // 리스트의 전체 아이템들을 새로고침 (CHECKED된 item UI 갱신)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

}