package com.graduation.travelbook2.search

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityLocalImgsBinding
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.search.adapter.SelImgAdapter
import com.graduation.travelbook2.search.adapter.SelectedImgAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.graduation.travelbook2.search.listener.ItemImgSelClickListener
import com.graduation.travelbook2.search.listener.ItemIntentClickListener
import com.graduation.travelbook2.search.modify.ArrangeImgsOrderActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileNotFoundException


class LocalImgsActivity :
    BaseActivity<ActivityLocalImgsBinding>(), ItemImgSelClickListener, ItemIntentClickListener{

    override val TAG : String = LocalImgsActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_local_imgs

    private lateinit var selImgAdapter: SelImgAdapter
    private var localByPhoto: ArrayList<ImgInfo> = ArrayList()
    private val localByPhotoInPerson: ArrayList<ImgInfo> = ArrayList()

    private lateinit var selectedImgAdapter: SelectedImgAdapter
    private val selectedImg: ArrayList<SelectedImgDto> = arrayListOf()

    // 높은 정확도를 가진 랜드마크 탐지와 얼굴 분류를 위한 설정
    var highAccuracyOpts: FaceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setMinFaceSize(1.0f)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    var bitmap: Bitmap? = null
    var img : InputImage? = null
    var wasDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localByPhoto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("photoList", ImgInfo::class.java)!!
        }else{
            intent.getParcelableArrayListExtra<ImgInfo>("photoList")!!
        }

        Log.d("넘겨받은list", localByPhoto.toString())

        setRVselImg()

        setPersonTB()

        setMakingBtn()

    }

    private fun setMakingBtn() {
        binding.btnMkBook.apply {
            setOnClickListener {
                (application as MyApplication).selectedImg1 = selectedImg
                // selectedPhoto list를 intent로 넘김
                val mIntent = Intent(this@LocalImgsActivity, ArrangeImgsOrderActivity::class.java)
                // 리스트(SelectedRV-position, 리스트) 전달
                Log.e("선택된 사진들", selectedImg.toString())
                mIntent.putExtra("selectedImg", selectedImg)
                startActivity(mIntent)
            }
        }
    }

    private fun setPersonTB() {
        binding.tbOnoff.setOnToggledListener { toggleableView, isOn ->
            if(isOn){ // 얼굴이 일정 크기 이상 인식된 사진만 보기 On시
                // FaceDetector 객체 가져오기
                val detector = FaceDetection.getClient(highAccuracyOpts)

                if(!wasDetected) {
                    val dialog = LoadingDialog(this@LocalImgsActivity)
                    dialog.show()
                    CoroutineScope(Dispatchers.Main).launch {
                        CoroutineScope(Dispatchers.IO).async {
                            localByPhoto.forEach { imgInfo ->
                                CoroutineScope(Dispatchers.IO).async {
                                    // Ready to input image
                                    val imageFile = File(imgInfo.path!!)
                                    val imageUri = Uri.fromFile(imageFile)

                                    setImage(imageUri)
                                }.await()

                                detector.process(img!!)
                                    .addOnSuccessListener {
                                        // 얼굴을 인식한 경우 새 리스트에 추가
                                        if (!it.isNullOrEmpty()) {
                                            Log.e("face", "인식 성공")
                                            Log.e("face 인식 정보", it.toString())
                                            localByPhotoInPerson.add(imgInfo)
                                        } else Log.e("face", "인식 실패")
                                    }
                                    .addOnFailureListener {
                                        // 얼굴 인식을 못한 경우 리스트에서 작업x
                                        Log.e("face", "통신 실패")
                                    }.await()
                            }
                            dialog.dismiss()
                        }.await()
                        // 리싸이클러뷰에 리스트 데이터를 넘겨 업데이트
                        reloadRVselImg(localByPhotoInPerson)

                    }
                    wasDetected = true
                }else{ // 이전에 얼굴인식을 했었다면
                    reloadRVselImg(localByPhotoInPerson)
                }

            }else{ // 얼굴이 일정 크기 이상 인식된 사진만 보기 off시
                reloadRVselImg(localByPhoto)
            }
        }
    }

    // todo: err- uri 값을 넘겨주어야 함
    // uri를 비트맵으로 변환시킨후 이미지뷰에 띄워주고 InputImage를 생성하는 메서드
    private fun setImage(uri: Uri) {
        try {
            // use() 함수를 사용해 스트림을 열고 사용한 후 자동으로 닫아줌
            contentResolver.openInputStream(uri)?.use{inputStream->
                bitmap = BitmapFactory.decodeStream(inputStream)
                img = InputImage.fromFilePath(this, uri)
                // img = InputImage.fromBitmap(bitmap!!, 0)
                Log.e("setImage", "이미지 to 비트맵")
            }
        } catch (e: FileNotFoundException) {
            Log.e("errMassage", e.toString())
            e.printStackTrace()
        }
    }


    private fun reloadRVselImg(imgList: ArrayList<ImgInfo>){
        binding.rvSelPicture.apply {
            if (imgList.isEmpty()){
                // 해당 기간에 촬영한 사진이 없음 메시지 표시
                this.visibility = View.GONE
                binding.tvExplainNoImg.visibility = View.VISIBLE
            }
            else{
                // 해당 기간에 촬영한 사진이 있을 경우 사진 표시
                // Log.e("localByPhotoInPerson", localByPhotoInPerson.toString())
                // Log.e("localByPhoto", localByPhoto.toString())
                this.visibility = View.VISIBLE
                binding.tvExplainNoImg.visibility = View.GONE

                selImgAdapter.changeImgList(imgList)
                selImgAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setRVselImg() {
        // setting 사진선택 리싸이클러뷰
        binding.rvSelPicture.apply {
            // localByPhoto 가 empty라면
            if(localByPhoto.isNullOrEmpty()){
                // 해당 기간에 촬영한 사진이 없음 메시지 표시
                this.visibility = View.GONE
                binding.tvExplainNoImg.visibility = View.VISIBLE
            }else{
                // 해당 기간에 촬영한 사진이 있을 경우 사진 표시
                this.visibility = View.VISIBLE
                binding.tvExplainNoImg.visibility = View.GONE

                selImgAdapter = SelImgAdapter(localByPhoto)
                adapter = selImgAdapter
                setHasFixedSize(true)
                // 사진이 전부가 10개 이상이 아니더라도
                // RV의 10개이상의 사진이 들어간 것처럼 크기를 유지
                // - layout의 height를 0dp로 주어서 해결

                //  2. selImgAdapter 의 클릭 리스너 설정
                //  2-1. 사진 클릭시 확대하여 이미지 표시하는 클릭 리스너 정의
                selImgAdapter.setOnItemIntentClickListener(this@LocalImgsActivity)

                //  2-2. 체크박스 동작 처리
                selImgAdapter.setOnItemCheckedListener(this@LocalImgsActivity)
            }
        }
    }

    override fun onItemCheck(isChecked: Boolean, imgInfo: ImgInfo, imgIndex: Int) {
        // 체크박스 클릭시 해당 사진 리스트에 담고, rvSelectedImgAdapter 표시
        binding.rvSelectedPicture.apply {
            Log.e("하단RV에 add될 item", "$isChecked, $imgInfo")
            // 첫번째일때 RV 만들고 그 이후에는 item 만 추가
            if (isChecked){
                if (selectedImg.isEmpty()){
                    val imgDto = SelectedImgDto(imgIndex, imgInfo)
                    selectedImg.add(imgDto)
                    selectedImgAdapter = SelectedImgAdapter(selectedImg)
                    adapter = selectedImgAdapter
                    selectedImgAdapter.notifyItemInserted(selectedImg.size-1)
                }else{
                    val imgDto = SelectedImgDto(imgIndex, imgInfo)
                    selectedImg.add(imgDto)
                    selectedImgAdapter.notifyItemInserted(selectedImg.size-1)
                }
            }else{
                // todo. 체크박스가 해제되면 해당 position의 사진을 제거
                var position = 0
                selectedImgAdapter.listSelectedPhoto.forEachIndexed{ index, it ->
                    if(it.imgIndex == imgIndex) position = index
                }
                selectedImgAdapter.deleteItem(position)
            }

        }

    }

    override fun onItemClickIntent(view: View, imgInfo: ImgInfo, pos: Int) {
        Log.e(TAG, "아이템 클릭")
        Intent(this@LocalImgsActivity, ImgFullActivity::class.java).apply {
            putExtra("imgPath", imgInfo.path) // 이미지 경로 전달
        }.run { startActivity(this) } // 액티비티로 이동
    }
}