package com.graduation.travelbook2.search.modify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
    private var currentFragment : Fragment? = null
    private val fragmentTags : ArrayList<String> = ArrayList()

    private lateinit var imgSelectAdapter: ImgSelectedAdapter // 어답터
    private var currentFragmentIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = (application as MyApplication).selectedImg1

        createImgFragments()
        setFirstFragment()
        setSelectedImgRV()
        setNextBtn()
        setPrevBtn()

        binding.apply {

        }

    }

    private fun setPrevBtn() {
        // 현재 보여지는 프레그먼트 정보 얻기 -> 인덱스 정보 얻기
        // 보여지는 프레그먼트를 이전 i -> i - 1 로 교체
        //  if. 1번 인덱스라면 마지막 인덱스로 교체
        binding.ibtnPrev.setOnClickListener {
            currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_img_add_info)
            supportFragmentManager.fragments.forEachIndexed { index, fragment ->
                if(fragmentTags.contains(fragment.tag)){
                    currentFragmentIndex = index
                    return@forEachIndexed
                }
            }
            if (currentFragmentIndex != -1) {
                // 현재 표시된 프레임의 인덱스 출력
                Log.d("Current Fragment", "Index: $currentFragmentIndex")
            } else {
                Log.d("Current Fragment", "No matching fragment found")
            }
        }

    }

    private fun setNextBtn() {
        // 프레그먼트를 다음 i -> i+1 로 교체
        //  if. i+1 이 마지막 인덱스라면 1번 인덱스로 교체
    }

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            fragmentTags.add("img$i")
            // 프레그먼트에 필요한 리스트의 정보 삽입
            listAddInfoImgFragment.add(AddInfoFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listAddInfoImgFragment[i].arguments = bundle

        }
    }

    private fun setFirstFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_img_add_info, listAddInfoImgFragment[0], "img0").commit()

        for (i: Int in 1 until listAddInfoImgFragment.size){
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_img_add_info, listAddInfoImgFragment[i], "img$i"
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

                    currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_img_add_info)


                }

            })

        }
    }
}