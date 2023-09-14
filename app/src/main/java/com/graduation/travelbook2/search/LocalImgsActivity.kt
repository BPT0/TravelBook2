package com.graduation.travelbook2.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityLocalImgsBinding
import com.graduation.travelbook2.search.adapter.SelImgAdapter
import com.graduation.travelbook2.search.listener.ItemImgSelClickListener
import com.graduation.travelbook2.search.listener.ItemIntentClickListener

class LocalImgsActivity :
    BaseActivity<ActivityLocalImgsBinding>(), ItemImgSelClickListener, ItemIntentClickListener{

    override val TAG : String = LocalImgsActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_local_imgs

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
            // - 리싸이클러뷰 정의
            rvSelPicture.apply {
                selImgAdapter = SelImgAdapter(localByPhoto?.toList() as ArrayList<ImgInfo>)
                adapter = selImgAdapter
                setHasFixedSize(true)
                // 사진이 전부가 10개 이상이 아니더라도
                // RV의 10개이상의 사진이 들어간 것처럼 크기를 유지
                // - layout의 height를 0dp로 주어서 해결

                // todo.
                //  2. selImgAdapter 의 클릭 리스너 설정
                //  2-1. 사진 클릭시 확대하여 이미지 표시하는 클릭 리스너 정의
                selImgAdapter.setOnItemIntentClickListener(this@LocalImgsActivity)

                //  2-2. 체크박스 동작 처리
                //   - 체크박스 클릭시 해당 사진 리스트에 담고, 아래 리스트에 표시

            }
        }
    }

    override fun onItemCheck(isChecked: Boolean, imgInfo: ImgInfo) {

    }

    override fun onItemClickIntent(view: View, imgInfo: ImgInfo, pos: Int) {
        Log.e(TAG, "아이템 클릭")
        Intent(this@LocalImgsActivity, ImgFullActivity::class.java).apply {
            putExtra("imgPath", imgInfo.path) // 이미지 경로 전달
        }.run { startActivity(this) } // 액티비티로 이동
    }
}