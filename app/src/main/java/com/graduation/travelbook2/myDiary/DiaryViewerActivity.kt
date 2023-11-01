package com.graduation.travelbook2.myDiary

import android.net.Uri
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityBookViewerBinding
import com.graduation.travelbook2.myDiary.adapter.DiaryViewPagerAdapter

class DiaryViewerActivity : BaseActivity<ActivityBookViewerBinding>() {
    override val TAG: String = DiaryViewerActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_book_viewer


    private var diaryImgs: ArrayList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        diaryImgs = intent.getSerializableExtra("diaryImgs")!! as (ArrayList<Uri>)

        binding.apply {
            vp2BookPager.apply {
                setPageTransformer { page, position ->
                    val maxTranslateOffsetX = 100
                    // 페이지 전환 애니메이션 구현
                    // position 값은 -1(완전히 보이지 않는 페이지)부터 1(완전히 보이는 페이지)까지 변화합니다.
                    // 페이지 전환 애니메이션 구현
                    // position 값은 -1(완전히 보이지 않는 페이지)부터 1(완전히 보이는 페이지)까지 변화합니다.
                    val absPos = Math.abs(position)
                    page.alpha = 1 - absPos
                    page.scaleY = 0.8f + 0.2f * (1 - absPos)
                    page.translationX = -maxTranslateOffsetX * absPos
                }
                adapter = DiaryViewPagerAdapter(diaryImgs) // 어댑터 생성
                orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            }


        }

    }
}