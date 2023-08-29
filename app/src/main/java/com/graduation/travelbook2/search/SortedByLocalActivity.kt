package com.graduation.travelbook2.search

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivitySortedByLocalBinding
import com.graduation.travelbook2.search.adapter.SelImgAdapter

class SortedByLocalActivity : BaseActivity<ActivitySortedByLocalBinding>() {

    override val TAG : String = SortedByLocalActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_sorted_by_local

    private lateinit var selImgAdapter: SelImgAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val localByPhoto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("photoList", ImgInfo::class.java)
        }else{
            intent.getParcelableArrayListExtra<ImgInfo>("photoList")
        }

        binding.apply {
            Log.d("넘겨받은list", localByPhoto.toString())
        // 1. rv_be_chosen_picture, 상단 리싸이클러뷰에 사진 표시
            // - 리싸이클러뷰 정의
            rvSelPicture.apply {
                selImgAdapter = SelImgAdapter(localByPhoto?.toList() as ArrayList<ImgInfo>)
                adapter = selImgAdapter
                // 2. selImgAdapter 의 리스너 설정

                // 3. 체크박스 동작 처리
                //  - 체크박스 클릭시 해당 사진 리스트에 담고, 아래 리스트에 표시dd
            }
        }
    }
}