/*
package com.pipecodingclub.travelbook.search.adapter

import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pipecodingclub.travelbook.search.SortedImgActivity
import com.pipecodingclub.travelbook.search.ImageActivity
import com.pipecodingclub.travelbook.R
import com.pipecodingclub.travelbook.databinding.ItemPhotoBinding
import com.pipecodingclub.travelbook.search.dto.ImgVO

class AlbumAdapter(var imageList: ArrayList<ImgVO>, var glActivity: SortedImgActivity)
    : RecyclerView.Adapter<AlbumAdapter.ImageVOViewHolder>() {
    companion object {
        private const val TAG = "AlbumAdater"
    }

    // 익명 리스너 객체를 담을 동적리스트 선언
    val selectionList = mutableListOf<Long>() // 아이템뷰의 내부아이템의 태그를 추가할 리스트이다
    // 아이템뷰 내부의 태그를 담은 객체리스트를 -> Unit -> 익명리스너 객체로 변환할 배열을  선언한다
    val onItemClickListener : ((MutableList<Long>) -> Unit)?= null

    val selGAdapter = SortedImgActivity.selImgAdapter
    val selImageList = SortedImgActivity.selImageList

    // ViewHolder 생성하는 함수, 최소 생성 횟수만큼만 호출됨 (계속 호출 X)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVOViewHolder {
        val binding = ItemPhotoBinding.inflate(
            // layoutInflater 를 넘기기위해 함수 사용, ViewGroup 는 View 를 상속하고 View 는 이미 Context 를 가지고 있음
            LayoutInflater.from(parent.context), parent, false)
        // 부모(리싸이클러뷰 = 뷰그룹), 리싸이클러뷰가 attach 하도록 해야함 (우리가 하면 안됨)

        binding.root.setOnClickListener {  v ->
            // 아이템뷰 하위의 각 요소의 태그를 id에 저장하고
            val id = v?.tag
            // 그 id가 담겨있으면 (이미 리스너객체가 만들어 졌다면) id 제거하고
            if (selectionList.contains(id)) selectionList.remove(id)
            // id 안담겨있으면 위에서 얻은 id를 형변환하여
            else selectionList.add(id as Long)
            // 커스텀 리스너 객체를 붙여준다
            onItemClickListener?.let { it(selectionList) }
        }

        return ImageVOViewHolder(binding)
    }

    // 만들어진 ViewHolder에 데이터를 바인딩하는 함수
    // position = 리스트 상에서 몇번째인지 의미
    override fun onBindViewHolder(holder: ImageVOViewHolder, position: Int) {
        holder.bind(imageList[position])
        holder.apply {
            itemView.apply {
                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked){
                        Log.d("dd", "사진 체크됨")
                        // 사진이 선택됬을때
                        // 선택된 사진을 true 값으로 변경
                        imageList[position].checked = true
                        // 선택된 이미지를 배열에 추가
                        selImageList.add(imageList[position])

                        // selAlbum 어답터에 이미지 배열에 추가
                        selGAdapter.addImage(selImageList)
                        selGAdapter.notifyItemInserted(selImageList.size-1)

                        imgCover.setBackgroundResource(R.drawable.back_photo_cover_enable)

                        // 클릭될을때 메인엑티비티에서 rv 설정
                    }else{
                        // 사진이 선택 해제 됐을때 배열에 값을 빼서 GL 액티비티에 전달
                        imageList[position].checked = false

                        selImageList.remove(imageList[position])
                        // image 삭제
                        selGAdapter.delImage(selImageList)
                        selGAdapter.notifyItemInserted(selImageList.size-1)

                        imgCover.setBackgroundResource(R.drawable.back_photo_cover_unable)
                    }
                }

                // 이미지뷰 클릭시
                ivImage.setOnClickListener {
                    // 전체 영역으로 확대 액티비티로 이동
                    val imgIntent = Intent(context, ImageActivity::class.java)
                    imgIntent.putExtra("clickImage", imageList[position])
                    val opt = ActivityOptions.makeSceneTransitionAnimation(
                        glActivity, itemView, "imgtrans")
                    ContextCompat.startActivity(context, imgIntent, opt.toBundle())
                }

            }
        }
    }


    override fun getItemCount(): Int = imageList.size


    class ImageVOViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val imgCover = binding.imgCover
        val cb = binding.cbSelImage
        val ivImage = binding.ivImage

        fun bind(image: ImgVO) {
            binding.apply {
                // 리스트에 저장된 내용 화면에 표시

                // 이미지 표시
                Glide.with(itemView.context)
                    .load(image.imageUri)
                    .override(150,150)
                    .into(ivImage)

            }
        }
    }
}*/
