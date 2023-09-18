package com.graduation.travelbook2.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.View
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityLocalImgsBinding
import com.graduation.travelbook2.search.adapter.SelImgAdapter
import com.graduation.travelbook2.search.adapter.SelectedImgAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.search.listener.ItemImgSelClickListener
import com.graduation.travelbook2.search.listener.ItemIntentClickListener

class LocalImgsActivity :
    BaseActivity<ActivityLocalImgsBinding>(), ItemImgSelClickListener, ItemIntentClickListener{

    override val TAG : String = LocalImgsActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_local_imgs

    private lateinit var selImgAdapter: SelImgAdapter
    private lateinit var localByPhoto: ArrayList<ImgInfo>

    private lateinit var selectedImgAdapter: SelectedImgAdapter
    private val selectedPhoto: ArrayList<SelectedImgDto> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localByPhoto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("photoList", ImgInfo::class.java)!!
        }else{
            intent.getParcelableArrayListExtra<ImgInfo>("photoList")!!
        }

        binding.apply {
            Log.d("넘겨받은list", localByPhoto.toString())

            setRVselPicture()

        }
    }

    private fun setRVselPicture() {
        // setting 사진선택 리싸이클러뷰
        binding.rvSelPicture.apply {
            selImgAdapter = SelImgAdapter(localByPhoto.toList() as ArrayList<ImgInfo>)
            adapter = selImgAdapter
            setHasFixedSize(true)
            // 사진이 전부가 10개 이상이 아니더라도
            // RV의 10개이상의 사진이 들어간 것처럼 크기를 유지
            // - layout의 height를 0dp로 주어서 해결

            //  2. selImgAdapter 의 클릭 리스너 설정
            //  2-1. 사진 클릭시 확대하여 이미지 표시하는 클릭 리스너 정의
            selImgAdapter.setOnItemIntentClickListener(this@LocalImgsActivity)

            //  2-2. 체크박스 동작 처리
            selImgAdapter.setOnItemCheckedListener(this@LocalImgsActivity)

        }
    }

    override fun onItemCheck(isChecked: Boolean, imgInfo: ImgInfo, imgIndex: Int) {
        // 체크박스 클릭시 해당 사진 리스트에 담고, rvSelectedImgAdapter 표시
        binding.rvSelectedPicture.apply {
            Log.e("하단RV에 add될 item", "$isChecked, $imgInfo")
            // 첫번째일때 RV 만들고 그 이후에는 item 만 추가
            if (isChecked){
                if (selectedPhoto.isEmpty()){
                    val imgDto = SelectedImgDto(imgIndex, imgInfo)
                    selectedPhoto.add(imgDto)
                    selectedImgAdapter = SelectedImgAdapter(selectedPhoto)
                    adapter = selectedImgAdapter
                    selectedImgAdapter.notifyItemInserted(selectedPhoto.size-1)
                }else{
                    val imgDto = SelectedImgDto(imgIndex, imgInfo)
                    selectedPhoto.add(imgDto)
                    selectedImgAdapter.notifyItemInserted(selectedPhoto.size-1)
                }
            }else{
                // todo. 체크박스가 해제되면 해당 position의 사진을 제거
                var position = 0
                selectedImgAdapter.listSelectedPhoto.forEachIndexed{ index, it ->
                    if(it.imgIndex == imgIndex) position = index
                }
                selectedImgAdapter.deleteItem(position)
            }

        }

    }

    override fun onItemClickIntent(view: View, imgInfo: ImgInfo, pos: Int) {
        Log.e(TAG, "아이템 클릭")
        Intent(this@LocalImgsActivity, ImgFullActivity::class.java).apply {
            putExtra("imgPath", imgInfo.path) // 이미지 경로 전달
        }.run { startActivity(this) } // 액티비티로 이동
    }
}