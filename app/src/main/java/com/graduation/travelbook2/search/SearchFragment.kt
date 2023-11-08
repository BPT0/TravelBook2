package com.graduation.travelbook2.search

import android.content.Intent
import android.content.res.Resources
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.datepicker.MaterialDatePicker
import com.graduation.travelbook2.adapterDeco.GridItemDeco
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.search.adapter.LocalAdapter
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener
import com.pipecodingclub.travelbook.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.runOnUiThread
import java.util.Calendar
import java.util.Locale

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
    private var setLocalByImgInfo : HashMap<String, ArrayList<ImgInfo>> = hashMapOf()

    private var dateRangePicker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = ImgInfoDb.getInstance(this.requireContext())!!

        binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                val dialog = LoadingDialog(this@SearchFragment.requireContext())

                dialog.show()
                async { isUpLoadImgCheck() }.await()
                dialog.dismiss()

                setDatePicker() // dateLangePicker 및 날짜 정보 초기화 버튼 설정
                setBtnLoadImg()
            }
        }
    }

    private fun setBtnLoadImg() {
        binding.btnLoadImg.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val dialog = LoadingDialog(this@SearchFragment.requireContext())
                dialog.show()
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    loadImages()
                    isUpLoadImgCheck()
                }
                dialog.dismiss()
                updateRVLocate()
            }
        }
    }

    private fun loadImages(){
        Log.d("loadImage 실행", "실행됨")
        // projection: 이미지 에서 불러올 정보 설정
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATA
        )

        // 기기의 MediaStore에 있는 데이터를 질의문을 사용해 cusor 가져옴
        val cursor = this.requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        if (cursor!=null) {
            while (cursor.moveToNext()) {
                // 사진 경로 uri 가져오기
                val columIndex = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DATA
                )
                val imagePath = cursor.getString(columIndex)

                // 사진 위치 정보 가져오기
                val exif = ExifInterface(imagePath)
                val gps = exif.latLong
                val date = exif.dateTime

                // 이미지에 위치정보와 날씨 정보가 있다면
                if (gps != null && date != null) {
                    val locality = getLocalityFromCoordinates(gps[0], gps[1])

                    // 이미지에 지역명이 비어있지 않다면
                    if (locality.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            // db에 파일 경로의 이미지들 중 같은 경로가 있지 않다면
                            if(db.imgInfoDao().isImgInserted(imagePath) < 1){
                                // db에 이미지 추가
                                db.imgInfoDao().insertImgInfo(
                                    ImgInfo(
                                        imagePath, gps[0], gps[1], locality, date,
                                        isChecked = false,
                                    )
                                )
                                println("새로운 이미지입니다.")
                            }else{
                                println("같은 이미지입니다.")
                            }
                        }
                    }
                }
            }
            cursor.close()
        }
    }

    private fun getLocalityFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this.requireContext(), Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 5)
            addresses!!.let {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    return address.locality ?: ""
                }
            }
        }catch (e: Exception){
            Log.d(e.toString(), e.toString())
        }

        return ""
    }

    private fun isUpLoadImgCheck() {
        CoroutineScope(Dispatchers.IO).launch{
            if (db.imgInfoDao().getAllImgInfo().isEmpty()) {
                this@SearchFragment.requireContext().runOnUiThread {
                    // 1.위치 정보가 등록된 사진이 없다는 안내 문구 표시 보여줌
                    binding.tvImgCount.visibility = View.VISIBLE
                    // 2.위치 리싸이크러뷰 지우고
                    binding.rvLocals.visibility = View.GONE
                }
            } else {
                listAllImgInfo = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        db.imgInfoDao().getAllImgInfo() as ArrayList<ImgInfo>
                }

                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    listAllImgInfo?.forEach { imgInfo ->
                        setLocalName.add(imgInfo.locality.toString())
                    }
                    println("setLocalName $setLocalName")
                }

                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    setLocalName.forEach { localName ->
                        setLocalByImgInfo.put(
                            localName,
                            db.imgInfoDao().getLocalByImgInfo(localName) as ArrayList<ImgInfo>
                        )
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    // todo: 페이징3로 리싸이클러뷰 변경
                    // https://velog.io/@dlwpdlf147/Android-Custom-Gallery-with-Paging3
                    setRVLocate()   // 장소별 그리드레이아웃 리싸이클러뷰 사용
                }
            }
        }

    }

    private fun setRVLocate() {
        // 장소별 선택 RV 설정
        Log.e("setLocalName", setLocalName.toString())
        binding.rvLocals.apply {
            if(setLocalName.isNotEmpty()){
                binding.tvImgCount.visibility = View.GONE
                binding.rvLocals.visibility = View.VISIBLE
                // item간 간격 조정
                // todo: 지역RV item 안의 텍스트 패딩 조절
                localAdapter = LocalAdapter(setLocalName.toList() as ArrayList<String>, setLocalByImgInfo)
                addItemDecoration(GridItemDeco(3, 7f.fromDpToPx()))
                adapter = localAdapter
                localAdapter.notifyItemRangeChanged(0, setLocalName.size-1)

                localAdapter.setListener(object : ItemClickListener {
                    override fun onCLickLocal(pos: Int, localName: String) {
                        // todo: 장소 클릭시
                        Log.d("itemclick", "장소 클릭 $pos, $localName")
                        val localByPhotoIntent = Intent(this@SearchFragment.requireContext(), LocalImgsActivity::class.java)
                        if(startDate == null){
                            var listLocalByImg: ArrayList<ImgInfo>
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
                            var listLocalByImgInfoBetweenDate: ArrayList<ImgInfo>
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

                    override fun onClickImg(pos: Int, img: ImgInfo) {}

                })
            }
            else{
                // 지역정보를 가진 사진이 하나도 없다면
                binding.apply{
                    tvImgCount.visibility = View.VISIBLE
                    rvLocals.visibility = View.GONE
                }
            }
        }
    }

    private fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun updateRVLocate(){
        binding.rvLocals.apply {
            if(setLocalName.isNotEmpty()){
                binding.tvImgCount.visibility = View.GONE
                binding.rvLocals.visibility = View.VISIBLE
                localAdapter.changeLocalList(setLocalName.toList() as ArrayList<String>)
                localAdapter.notifyItemRangeChanged(0, setLocalName.size-1)
                scrollToPosition(localAdapter.itemCount -1)
            }
            else{
                // 지역정보를 가진 사진이 하나도 없다면
                binding.tvImgCount.visibility = View.VISIBLE
                binding.rvLocals.visibility = View.GONE
            }
        }
    }
    private fun setDatePicker() {
        binding.apply {
            // todo. set 날짜 정보 초기화 버튼 기능 구현
            btnReloadByDate.setOnClickListener {
                startDate = null
                Log.e("startDate", startDate.toString())
                etxDateRange.setText("사진을 찍은 기간을 선택하세요")
            }

            etxDateRange.setOnClickListener{
                dateRangePicker =
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("사진을 찍은 기간을 선택하세요")
                        .build()

                dateRangePicker!!.let {dateRangePicker ->
                    dateRangePicker.show(childFragmentManager, "date_picker")
                    dateRangePicker.addOnPositiveButtonClickListener { selection ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selection?.first ?: 0
                        //  1. 사진의 날짜 정보를 long 타입으로 변환후
                        //  2. db에서 비교 조건으로 해당하는 사진들을 검색
                        startDate = calendar.time.time
                        Log.d("startDate", startDate.toString())

                        calendar.timeInMillis = selection?.second ?: 0
                        endDate = calendar.time.time
                        Log.d("endDate", endDate.toString())

                        etxDateRange.setText(dateRangePicker.headerText)
                    }
                }
            }
        }
    }

}