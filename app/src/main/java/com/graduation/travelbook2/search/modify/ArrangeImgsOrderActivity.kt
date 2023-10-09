package com.graduation.travelbook2.search.modify

import android.os.Build
import android.os.Bundle
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityArrangeImgsOrderBinding
import com.graduation.travelbook2.search.adapter.ImgArrangeAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto

class ArrangeImgsOrderActivity: BaseActivity<ActivityArrangeImgsOrderBinding>(){
    override val TAG : String = ArrangeImgsOrderActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_arrange_imgs_order

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>
    private lateinit var listRevialImgFragment : ArrayList<RevialImgsFragment>


    private lateinit var imgArrangeAdapter: ImgArrangeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("selectedImg", SelectedImgDto::class.java)!!
        }else{
            intent.getParcelableArrayListExtra<SelectedImgDto>("selectedImg")!!
        }

        // todo: selectedImg 리스트의 크기만큼 fragment들을 생성
        //  각 프레그먼트에는 리스트의 정보가 있어야함
        setFirstFragment()

        setSelectedImgRV()
    }

    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgArrangeAdapter = ImgArrangeAdapter(selectedImgs)
            adapter = imgArrangeAdapter
            setHasFixedSize(true)

            // todo: 필요한 리스너 설정
        }
    }

    private fun setFirstFragment() {
        revialImgFragment = RevialImgsFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_photo_frame, revialImgFragment).commit()

        // todo: 프레그먼트에 필요한 정보 전달
    }
}