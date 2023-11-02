package com.graduation.travelbook2.search.modify2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduation.travelbook2.MainActivity
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityEditImgBinding
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.search.adapter.ImgSelectedAdapter
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.SaveSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class EditImgActivity : BaseActivity<ActivityEditImgBinding>() {
    override val TAG : String = EditImgActivity::class.java.simpleName
    override val layoutRes: Int = com.graduation.travelbook2.R.layout.activity_edit_img

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>

    private val listAddInfoImgFragment : ArrayList<AddInfoFragment> = ArrayList() // 프레그먼트 배열

    private lateinit var imgSelectAdapter: ImgSelectedAdapter // 어답터

    private val listImgFile : ArrayList<String> = ArrayList()

    private val storageBookRef = FirebaseStorage.getInstance().reference.child("allImages")

    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedImgs = (application as MyApplication).selectedImg1

        createImgFragments()
        setSelectedImgRV()
        saveDefaultImgs()

        binding.apply {
            vp2ImgAddInfo.apply {
                adapter = ScreenSlidePagerAdapter(this@EditImgActivity)
                isUserInputEnabled = false // 사용자의 슬라이드 이벤트 false
            }

            btnMakeDiary.setOnClickListener {
                saveEditImg()
            }
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

        if (selectedImgs.size == 1)
            binding.btnMakeDiary.visibility = View.VISIBLE
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = selectedImgs.size // 페이지 수 리턴

        override fun createFragment(position: Int): Fragment {
            // 페이지 포지션에 따라 그에 맞는 프래그먼트를 보여줌
            return listAddInfoImgFragment[position]
        }
    }

    private fun setSelectedImgRV() {
        binding.rvSelectedImg.apply {
            imgSelectAdapter = ImgSelectedAdapter(selectedImgs)
            adapter = imgSelectAdapter
            setHasFixedSize(true)

            // todo: 필요한 리스너 설정
            //  RV의 Item 클릭이벤트 - 클릭된 postion으로 뷰 페이져의 페이지로 스크롤하기
            imgSelectAdapter.setClickListener(object: ItemClickListener {
                override fun onCLickLocal(pos: Int, s: String) {}

                override fun onClickImg(pos: Int, img: ImgInfo) {
                    Log.e("뷰페이져", "페이지 $pos 로 스크롤")
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

    private fun saveDefaultImgs() {
        selectedImgs.forEach {img ->
            val filePath = img.imgInfo?.path
            listImgFile.add(filePath!!)
            Log.e("저장된 이미지", listImgFile.size.toString())
        }
    }

    private fun saveEditImg() {
        // 모든 프레그먼트들의 이미지들을 저장함
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                listAddInfoImgFragment.forEachIndexed { index, fragment ->
                    val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(false)
                        .build()
                    fragment.mPhotoEditor.saveAsFile(filesDir.toString(), saveSettings,
                        object : OnSaveListener {
                            override fun onFailure(exception: Exception) {
                                Log.e("PhotoEditor", "Failed to save Image")
                            }

                            override fun onSuccess(imagePath: String) {
                                listImgFile[index] = imagePath
                            }
                        })
                }
            }

            uploadImgList()
        }
    }

    private fun uploadImgList() {
        loadingDialog = LoadingDialog(this)
        val bookIndex = MyApplication.prefs.getBookIndex("bookIndex", 0)

        loadingDialog.show()
        listImgFile.forEachIndexed{ i, imgPath ->
            // 1. StorageReference 에 파일 업로드 - allImg/book$index/해당 사진
            val dirRef: StorageReference =
                storageBookRef.child("book$bookIndex").child("img$i")

            // 2. StorageReference 에 업로드한 파일 -> 실시간 DB에 uid 아래에 정보 저장
            dirRef.putFile(Uri.fromFile(File(imgPath))).addOnSuccessListener {
                dirRef.downloadUrl.addOnSuccessListener {
                    if (i==listImgFile.lastIndex){
                        MyApplication.prefs.setBookIndex("bookIndex", bookIndex+1)
                        Toast.makeText(this@EditImgActivity, "업로드 완료", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                        goMainActivity()
                    }
                }
            }.addOnProgressListener { // 로딩애니메이션 표시
            }.addOnFailureListener {
                Toast.makeText(this@EditImgActivity, "업로드 실패", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        }
        Log.e("업로드 작업", "완료함")
    }

    private fun goMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("sentActivity", "EditImgActivity")
        startActivity(intent)
        finish()
    }

}