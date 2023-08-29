package com.graduation.travelbook2.search

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import com.pipecodingclub.travelbook.base.BaseFragment
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.search.adapter.LocalAdapter
import com.pipecodingclub.travelbook.search.dto.ImgVO
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
    private val localList: ArrayList<String> = ArrayList()

    var totalpages : Int = 0
    private var localByImageList : HashMap<String, ArrayList<ImgVO>> = HashMap()

    private lateinit var startDate : String
    private lateinit var endDate :String

    private lateinit var db : ImgInfoDb     // 이미지 정보 db 객체
    private lateinit var listAllImgInfo: ArrayList<ImgInfo>
    private var setLocalName : MutableSet<String> = mutableSetOf()
    private var listLocalByImgInfo : HashMap<String, ArrayList<ImgInfo>> = hashMapOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ImgInfoDb.getInstance(this.requireContext())!!

        CoroutineScope(Dispatchers.Main).launch {
            // db에 저장된 모든 이미지에서
            // 장소의 문자열이 중복되지 않는 이미지를 가져옴
            listAllImgInfo = CoroutineScope(Dispatchers.IO).async {
                db.imgInfoDao().getAllImgInfo() as ArrayList<ImgInfo>
            }.await()

            println("listAllImgInfo: $listAllImgInfo")

            listAllImgInfo.forEach{imgInfo ->
                setLocalName.add(imgInfo.locality.toString())
            }
            println("setLocalName $setLocalName")

            CoroutineScope(Dispatchers.IO).async {
                setLocalName.forEach{localName->
                    listLocalByImgInfo.put(localName,
                        db.imgInfoDao().getLocalByImgInfo(localName) as ArrayList<ImgInfo>
                    )
                }
            }.await()

            // todo: Locate들 전부 표시될 때까지 로딩창 표시
            setRvLocate()   // 장소별 그리드레이아웃 리싸이클러뷰 사용

        }

        binding.apply {
            /*btnLoadPicture.setOnClickListener {
                // checkPermission()
            }*/

            // setDatePicker() // dateLangePicker 초기화
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
                    // 클릭된 장소정보를 가진 이미지(=listLocalByImgInfo)들이 있는 화면으로 intent
                    // 보낼 정보: 장소정보를 가진 이미지 리스트
                    Log.d("itemclick", "장소 클릭 $pos, $s")
                    val localbyPhotoIntent = Intent(this@SearchFragment.requireContext(), SortedByLocalActivity::class.java)
                    localbyPhotoIntent.putExtra("photoList", listLocalByImgInfo[s])
                    startActivity(localbyPhotoIntent)
                }
            })
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