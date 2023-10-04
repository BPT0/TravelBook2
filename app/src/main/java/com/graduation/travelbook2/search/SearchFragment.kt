package com.graduation.travelbook2.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import java.util.Calendar
import com.pipecodingclub.travelbook.base.BaseFragment
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.search.adapter.LocalAdapter
import com.pipecodingclub.travelbook.search.deco.LocalItemDeco
import com.pipecodingclub.travelbook.search.listener.ItemLocalClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate){
    companion object{
        const val tag: String = "사진찾기탭"
        fun newInstance(): SearchFragment{
            return SearchFragment()
        }

    }

    private lateinit var localAdapter : LocalAdapter

    private var startDate : Long? = null
    private var endDate : Long? = null

    private lateinit var db : ImgInfoDb   // 이미지 정보 db 객체
    private var listAllImgInfo: ArrayList<ImgInfo>? = null
    private var setLocalName : MutableSet<String> = mutableSetOf()
    private var listLocalByImgInfo : HashMap<String, ArrayList<ImgInfo>> = hashMapOf()

    // firebase DB 경로
    val uid = Firebase.auth.currentUser?.uid!!
    val mDBRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")
        .child("UserAccount").child(uid)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            upLoadImg()
            /*btnLoadPicture.setOnClickListener {
                // checkPermission()
                // reloadImg()
            }*/

            setDatePicker() // dateLangePicker 초기화
        }
    }

    private fun upLoadImg() {
        CoroutineScope(Dispatchers.IO).launch {
            val imgDb = ImgInfoDb.getInstance(this@SearchFragment.requireContext())
            if (imgDb != null){
                db = imgDb
                // 위치,날짜 정보가 등록된 사진이 없으면
                if(db.imgInfoDao().getAllImgInfo().isEmpty()){
                    CoroutineScope(Dispatchers.Main).launch {
                        // 1.위치 정보가 등록된 사진이 없다는 안내 문구 표시 보여줌
                        binding.tvImgCount.visibility = View.VISIBLE
                        // 2.위치 리싸이크러뷰 지우고
                        binding.rvLocals.visibility = View.GONE
                    }
                }
                else{
                    listAllImgInfo = CoroutineScope(Dispatchers.IO).async {
                        CoroutineScope(Dispatchers.Main).launch {
                            // 1.위치 정보가 등록된 사진이 없다는 안내 문구 표시 지우고
                            binding.tvImgCount.visibility = View.GONE
                            // 2.위치 리싸이클러뷰 표시
                            binding.rvLocals.visibility = View.VISIBLE
                        }
                        db.imgInfoDao().getAllImgInfo() as ArrayList<ImgInfo>
                    }.await()


                    println("listAllImgInfo: $listAllImgInfo")

                    // https://firebase.google.com/docs/database/android/read-and-write?hl=ko
                    // 회원별 리얼타임 DB에 디바이스의 전체사진을 업로드
                    mDBRef.child("img").setValue(listAllImgInfo)

                    listAllImgInfo?.forEach { imgInfo ->
                        setLocalName.add(imgInfo.locality.toString())
                    }
                    println("setLocalName $setLocalName")

                    CoroutineScope(Dispatchers.IO).async {
                        setLocalName.forEach { localName ->
                            listLocalByImgInfo.put(
                                localName,
                                db.imgInfoDao().getLocalByImgInfo(localName) as ArrayList<ImgInfo>
                            )
                        }
                    }.await()

                    // todo: 페이징3로 리싸이클러뷰 변경
                    // https://velog.io/@dlwpdlf147/Android-Custom-Gallery-with-Paging3
                    CoroutineScope(Dispatchers.Main).launch {
                        setRvLocate()   // 장소별 그리드레이아웃 리싸이클러뷰 사용
                    }
                }
            }
        }
    }

    private fun setRvLocate() {
        // 장소별 선택 RV
        binding.rvLocals.apply {
            localAdapter = LocalAdapter(setLocalName.toList() as ArrayList<String>, listLocalByImgInfo)
            adapter = localAdapter
            localAdapter.setListener(object : ItemLocalClickListener{
                override fun onCLickLocal(pos: Int, localName: String) {
                    // todo: 장소 클릭시
                    Log.d("itemclick", "장소 클릭 $pos, $localName")
                    val localByPhotoIntent = Intent(this@SearchFragment.requireContext(), LocalImgsActivity::class.java)
                    if(startDate == null){
                        var listLocalByImg = ArrayList<ImgInfo>()
                        CoroutineScope(Dispatchers.Main).launch {
                            listLocalByImg =  CoroutineScope(Dispatchers.IO).async {
                                db.imgInfoDao().getLocalByImgInfo(localName) as ArrayList<ImgInfo>
                            }.await()
                            Log.d("기간 설정x - 장소 별 사진", "$listLocalByImg")
                            localByPhotoIntent.putExtra("photoList", listLocalByImg)
                            startActivity(localByPhotoIntent)
                        }
                    }else{
                        // todo: (start, end) Date가 있다면 기간 안의 이미지들만 전달
                        // 지역에서(선택한 기간 안에 찍었던) 사진들을 넘겨줌
                        var listLocalByImgInfoBetweenDate = ArrayList<ImgInfo>()
                        CoroutineScope(Dispatchers.Main).launch {
                            listLocalByImgInfoBetweenDate = CoroutineScope(Dispatchers.IO).async {
                                db.imgInfoDao().getPeriodInLocalImg(localName, startDate!!, endDate!!) as ArrayList<ImgInfo>
                            }.await()
                            Log.e("기간 설정0 - 지역의 이미지", "$localName $startDate $endDate $listLocalByImgInfoBetweenDate")
                            localByPhotoIntent.putExtra("photoList", listLocalByImgInfoBetweenDate)
                            startActivity(localByPhotoIntent)
                        }
                    }
                }
            })

            // todo: item간 간격 조정
            //  지역RV item 안의 텍스트 패딩 조절
            addItemDecoration(LocalItemDeco(3, 20, false))

        }
    }


    private fun setDatePicker() {
        binding.apply {
            // todo. set 날짜 정보 초기화 버튼 기능 구현
            btnReloadByDate.setOnClickListener {

            }

            etxDateRange.setOnClickListener{
                val dateRangePicker =
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("사진을 찍은 기간을 선택하세요")
                        .build()

                dateRangePicker.show(childFragmentManager, "date_picker")
                dateRangePicker.addOnPositiveButtonClickListener { selection ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selection?.first ?: 0
                    // todo:
                    //  1. long 타입 변경
                    //  2. 롱타입으로 사진의 날짜 정보 변경
                    //  3. db에서 비교 조건으로 해당하는 사진들을 검색
                    startDate = calendar.time.time
                    Log.d("startDate", startDate.toString())

                    calendar.timeInMillis = selection?.second ?: 0
                    endDate = calendar.time.time
                    Log.d("endDate", endDate.toString())

                    etxDateRange.setText(dateRangePicker.headerText)
                }
            }
            //  * 기간의 - 첫날, 끝날 - 현재 Int형
        }
    }

}