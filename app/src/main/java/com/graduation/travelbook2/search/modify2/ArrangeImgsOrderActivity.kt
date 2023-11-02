package com.graduation.travelbook2.search.modify2

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityArrangeImgsOrderBinding
import com.graduation.travelbook2.search.adapter.ImgArrangeAdapter
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener
import com.graduation.travelbook2.search.listenerNcallback.BtnStateUdateListener
import com.graduation.travelbook2.search.listenerNcallback.OnCheckboxChangedListener

/**
 * 선택된 이미지들의 순서를 정렬하는 액티비티
 */
class ArrangeImgsOrderActivity: BaseActivity<ActivityArrangeImgsOrderBinding>(),
        BtnStateUdateListener, OnCheckboxChangedListener{
    override val TAG : String = ArrangeImgsOrderActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_arrange_imgs_order

    lateinit var selectedImgs: ArrayList<SelectedImgDto>
    private val listAppearedImgFragment : ArrayList<AppearedImgFragment> = ArrayList()

    /* 리싸이클러뷰 설정관련 변수 */
    private lateinit var imgArrangeAdapter: ImgArrangeAdapter // 어답터
    private lateinit var mItemTouchHelper: ItemTouchHelper // 드레그담당: 터치헬퍼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = (application as MyApplication).selectedImg1
        createImgFragments()
        setFirstFragment()
        setSelectedImgRV()
    }

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            // 프레그먼트에 필요한 리스트의 정보 삽입
            listAppearedImgFragment.add(AppearedImgFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listAppearedImgFragment[i].arguments = bundle
        }
    }

    private fun setFirstFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_photo_frame, listAppearedImgFragment[0]).commit()
        for (i: Int in 1 until listAppearedImgFragment.size){
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_photo_frame, listAppearedImgFragment[i]
            ).commit()
            supportFragmentManager.beginTransaction().hide(listAppearedImgFragment[i]).commit()
        }
    }

    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgArrangeAdapter = ImgArrangeAdapter(selectedImgs)
            adapter = imgArrangeAdapter
            setHasFixedSize(true)

            /*어답터에 드레그 터치헬퍼 등록*/
            /*mItemTouchHelper = ItemTouchHelper(ItemTouchCallback(imgArrangeAdapter))
            mItemTouchHelper.attachToRecyclerView(this)*/
            // 아이템이 드레그가 되어서 순서가 변경되었을때 프레그먼트의 배열 순서도 변경

            // todo: 필요한 리스너 설정
            //  RV의 Item 클릭이벤트 - 클릭된 사진을 상단 프레그먼트 화면에 보여주기
            imgArrangeAdapter.setClickListener(object: ItemClickListener{
                override fun onCLickLocal(pos: Int, s: String) {}

                override fun onClickImg(pos: Int, img: ImgInfo) {
                    Log.e("이미지클릭", "프레그먼트$pos 교체")
                    // 상단 프레그먼트 교체
                    // pos 와 imgInfo를  프레그먼트에 전달후 프레그먼트 교체
                    val bundle= Bundle()
                    bundle.putSerializable("imgInfo", selectedImgs[pos].imgInfo)
                    listAppearedImgFragment[pos].arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .show(listAppearedImgFragment[pos])
                        .commit()
                    for (i: Int in 0 until listAppearedImgFragment.size){
                        if(i!=pos){
                            supportFragmentManager.beginTransaction()
                                .hide(listAppearedImgFragment[i])
                                .commit()
                        }
                    }
                }

            })

        }
    }

    override fun updateBtnState(isEnabled: Boolean) {
        if(isEnabled){ // 프레그먼트중 1개를 체크했을 경우
            listAppearedImgFragment.forEach{
                it.stateTitleRbtn(true)
            }
        }else{ // 체크한 프레그먼트를 해제했을경우
            listAppearedImgFragment.forEach {
                it.stateTitleRbtn(false)
            }
        }
    }

    override fun updateCbtnState(isEnabled: Boolean) {
        if (isEnabled){ // 프레그먼트의 체크된 박스를 해제했을때
            listAppearedImgFragment.forEach {
                it.stateNextScreenBtn(true)
            }
        }else{ // 프레그먼트의 체크박스 중 하나를 체크했을때
            listAppearedImgFragment.forEachIndexed { i, it->
                if(!it.binding.cbxTitle.isChecked){
                    it.stateNextScreenBtn(false)
                }else{ // 체크박스 타이틀이 체크되었을때
                    (application as MyApplication).selectedImg1 = selectedImgs
                    val tmpImg = selectedImgs[0]
                    selectedImgs[0] = selectedImgs[i]
                    selectedImgs[i] = tmpImg
                    // 프레그먼트 순서 변경
                    updateFragmentsOrder(i)
                    // 하단의 RCview의 item 순서 변경
                    // 변경된 리스트로 다시 RCview의 상태 업데이트
                    binding.rvSelectedImg.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun updateFragmentsOrder(orderChangedFragmentIndex: Int) {
        val tmpFragment = listAppearedImgFragment[0]
        listAppearedImgFragment[0] = listAppearedImgFragment[orderChangedFragmentIndex]
        listAppearedImgFragment[orderChangedFragmentIndex] = tmpFragment
    }

    override fun onCheckboxChanged(isChecked: Boolean) {
        Log.e("프레그먼트 ", "체크박스 상태: $isChecked")
        if (isChecked) {
            updateBtnState(true)
            updateCbtnState(false)
        }else{
            updateBtnState(false)
            updateCbtnState(true)
        }
    }


}