package com.graduation.travelbook2.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import java.text.SimpleDateFormat
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

    private lateinit var startDate : String
    private lateinit var endDate :String

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
            }*/

            // setDatePicker() // dateLangePicker 초기화
        }
    }

    private fun upLoadImg() {
        CoroutineScope(Dispatchers.IO).launch {
            val imgDb = ImgInfoDb.getInstance(this@SearchFragment.requireContext())
            if (imgDb != null){
                db = imgDb
                if(db.imgInfoDao().getAllImgInfo().isEmpty())
                else{
                    listAllImgInfo = CoroutineScope(Dispatchers.IO).async {
                        CoroutineScope(Dispatchers.Main).launch {
                            // 1.위치 정보가 등록된 사진이 없다는 안내 문구 표시 지우고
                            binding.tvImgCount.visibility = View.GONE
                            // 2.위치 리싸이크러뷰 표시
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
                override fun onCLickLocal(pos: Int, s: String) {
                    // todo: 장소 클릭시
                    Log.d("itemclick", "장소 클릭 $pos, $s")
                    val localByPhotoIntent = Intent(this@SearchFragment.requireContext(), LocalImgsActivity::class.java)
                    localByPhotoIntent.putExtra("photoList", listLocalByImgInfo[s])
                    startActivity(localByPhotoIntent)
                }
            })

            // todo: item간 간격 조정
            //  지역RV item 안의 텍스트 패딩 조절
            addItemDecoration(LocalItemDeco(3, 20, false))

        }
    }


    private fun setDatePicker() {
        binding.apply {
            etxDateRange.setOnClickListener{
                val dateRangePicker =
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("사진을 찍은 기간을 선택하세요")
                        .build()

                dateRangePicker.show(childFragmentManager, "date_picker")
                dateRangePicker.addOnPositiveButtonClickListener { selection ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selection?.first ?: 0
                    startDate = SimpleDateFormat("yyyyMMdd").format(calendar.time).toString()
                    Log.d("start", startDate)

                    calendar.timeInMillis = selection?.second ?: 0
                    endDate = SimpleDateFormat("yyyyMMdd").format(calendar.time).toString()
                    Log.d("end", endDate)

                    etxDateRange.text = dateRangePicker.headerText as Editable
                }
            }
            // todo: 날짜별로 분류해서 다시 db에 저장
        }
    }

}