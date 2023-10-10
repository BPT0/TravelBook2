package com.graduation.travelbook2.search.modify

import android.os.Build
import android.os.Bundle
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityArrangeImgsOrderBinding
import com.graduation.travelbook2.search.adapter.ImgArrangeAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener

class ArrangeImgsOrderActivity: BaseActivity<ActivityArrangeImgsOrderBinding>(){
    override val TAG : String = ArrangeImgsOrderActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_arrange_imgs_order

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>
    private val listRevialImgFragment : ArrayList<RevialImgsFragment> = ArrayList()


    private lateinit var imgArrangeAdapter: ImgArrangeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("selectedImg", SelectedImgDto::class.java)!!
        }else{
            intent.getParcelableArrayListExtra("selectedImg")!!
        }

        // todo: 버튼 동작 처리
        for (i:Int in 0 until selectedImgs.size){
            // todo:
            //  1. 프레그먼트마다 대표이미지 체크된 값을 확인후
        }
        //  2. true가 있으면 모든 프레그먼트의 사진정보추가 버튼 활성화
        //  3. 모두 false면 모든 프레그먼트의 사진정보추가 버튼 활성화

        createImgFragments()
        setFirstFragment()

        setSelectedImgRV()
    }

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            // 프레그먼트에 필요한 리스트의 정보 삽입
            listRevialImgFragment.add(RevialImgsFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listRevialImgFragment[i].arguments = bundle

        }
    }

    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgArrangeAdapter = ImgArrangeAdapter(selectedImgs)
            adapter = imgArrangeAdapter
            setHasFixedSize(true)

            // todo: 필요한 리스너 설정
            //  RV의 Item 클릭이벤트 - 클릭된 사진을 상단 프레그먼트 화면에 보여주기
            imgArrangeAdapter.setClickListener(object: ItemClickListener{
                override fun onCLickLocal(pos: Int, s: String) {}
                override fun onClickImg(pos: Int, img: ImgInfo) {
                    // 상단 프레그먼트 교체

                }

            })

            //  RV의 드레그에서 움직이는 이벤트 등록
        }
    }

    private fun setFirstFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_photo_frame, listRevialImgFragment[0]).commit()

    }
}