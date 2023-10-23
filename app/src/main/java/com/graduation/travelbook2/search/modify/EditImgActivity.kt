package com.graduation.travelbook2.search.modify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.PhotoEditorSampleActivity
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityEditImgBinding
import com.graduation.travelbook2.search.adapter.ImgSelectedAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener

class EditImgActivity : BaseActivity<ActivityEditImgBinding>() {
    override val TAG : String = EditImgActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_edit_img

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>

    private val listAddInfoImgFragment : ArrayList<AddInfoFragment> = ArrayList() // 프레그먼트 배열

    private lateinit var imgSelectAdapter: ImgSelectedAdapter // 어답터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = (application as MyApplication).selectedImg1

        createImgFragments()
        setSelectedImgRV()

        binding.apply {
            vp2ImgAddInfo.apply {
                adapter = ScreenSlidePagerAdapter(this@EditImgActivity)
                isUserInputEnabled = false // 사용자의 슬라이드 이벤트 false
            }
        }

    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = selectedImgs.size // 페이지 수 리턴

        override fun createFragment(position: Int): Fragment {
            // 페이지 포지션에 따라 그에 맞는 프래그먼트를 보여줌
            return listAddInfoImgFragment[position]
        }
    }

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            // bundle로 프레그먼트에 필요한 리스트의 정보 전달
            listAddInfoImgFragment.add(AddInfoFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listAddInfoImgFragment[i].arguments = bundle
        }
    }


    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgSelectAdapter = ImgSelectedAdapter(selectedImgs)
            adapter = imgSelectAdapter
            setHasFixedSize(true)

            // 아이템이 드레그가 되어서 순서가 변경되었을때 프레그먼트의 배열 순서도 변경

            // todo: 필요한 리스너 설정
            //  RV의 Item 클릭이벤트 - 클릭된 postion으로 뷰 페이져의 페이지로 스크롤하기
            imgSelectAdapter.setClickListener(object: ItemClickListener {
                override fun onCLickLocal(pos: Int, s: String) {}

                override fun onClickImg(pos: Int, img: ImgInfo) {
                    Log.e("뷰페이져 스크롤", "페이지 $pos 로 스크롤")
                    binding.vp2ImgAddInfo.currentItem = pos
                    if (pos == selectedImgs.size-1){
                        binding.btnMakeDiary.visibility = View.VISIBLE
                    }else{
                        binding.btnMakeDiary.visibility = View.GONE
                    }
                }

            })

        }
    }
}