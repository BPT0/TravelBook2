package com.graduation.travelbook2.search.modify

import android.os.Bundle
import android.util.Log
import com.graduation.travelbook2.MyApplication
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
        setFirstFragment()
        setSelectedImgRV()

        binding.apply {

        }

    }

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            // 프레그먼트에 필요한 리스트의 정보 삽입
            listAddInfoImgFragment.add(AddInfoFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listAddInfoImgFragment[i].arguments = bundle
        }
    }

    private fun setFirstFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_img_add_info, listAddInfoImgFragment[0]).commit()
        for (i: Int in 1 until listAddInfoImgFragment.size){
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_img_add_info, listAddInfoImgFragment[i]
            ).commit()
        }
    }

    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgSelectAdapter = ImgSelectedAdapter(selectedImgs)
            adapter = imgSelectAdapter
            setHasFixedSize(true)

            // 아이템이 드레그가 되어서 순서가 변경되었을때 프레그먼트의 배열 순서도 변경

            // todo: 필요한 리스너 설정
            //  RV의 Item 클릭이벤트 - 클릭된 사진을 상단 프레그먼트 화면에 보여주기
            imgSelectAdapter.setClickListener(object: ItemClickListener {
                override fun onCLickLocal(pos: Int, s: String) {}

                override fun onClickImg(pos: Int, img: ImgInfo) {
                    Log.e("이미지클릭", "프레그먼트$pos 교체")
                    // 상단 프레그먼트 교체
                    // pos 와 imgInfo를  프레그먼트에 전달후 프레그먼트 교체
                    val bundle= Bundle()
                    bundle.putSerializable("imgInfo", selectedImgs[pos].imgInfo)
                    listAddInfoImgFragment[pos].arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .show(listAddInfoImgFragment[pos])
                        .commit()
                    for (i: Int in 0 until listAddInfoImgFragment.size){
                        if(i!=pos){
                            supportFragmentManager.beginTransaction()
                                .hide(listAddInfoImgFragment[i])
                                .commit()
                        }
                    }
                }

            })

        }
    }
}