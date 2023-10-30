package com.graduation.travelbook2.search.modify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduation.travelbook2.MainActivity
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityEditImgBinding
import com.graduation.travelbook2.externalDto.ImgDto
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.search.adapter.ImgSelectedAdapter
import com.graduation.travelbook2.internalDto.SelectedImgDto
import com.graduation.travelbook2.search.listenerNcallback.ItemClickListener
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.SaveSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class EditImgActivity : BaseActivity<ActivityEditImgBinding>() {
    override val TAG : String = EditImgActivity::class.java.simpleName
    override val layoutRes: Int = com.graduation.travelbook2.R.layout.activity_edit_img

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>

    private val listAddInfoImgFragment : ArrayList<AddInfoFragment> = ArrayList() // 프레그먼트 배열

    private lateinit var imgSelectAdapter: ImgSelectedAdapter // 어답터

    private val addInfoImgList : ArrayList<String?> = ArrayList()

    private val storageRef = FirebaseStorage.getInstance().reference
    private var userRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")
        .child("UserAccount")
    private val auth = FirebaseAuth.getInstance() // 유저 만들기

    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRef = userRef.child(auth.currentUser?.uid!!)
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
            addInfoImgList.add(filePath)
            Log.e("저장된 이미지", addInfoImgList.size.toString())
        }
    }

    private fun saveEditImg() {
        // 모든 프레그먼트들의 이미지들을 저장함
        CoroutineScope(Dispatchers.Main).launch {
            async {
                listAddInfoImgFragment.forEachIndexed {index, fragment ->
                    val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(false)
                        .build()
                    fragment.mPhotoEditor.saveAsFile(filesDir.toString(), saveSettings,
                        object: OnSaveListener{
                            override fun onFailure(exception: Exception) {
                                Log.e("PhotoEditor", "Failed to save Image")
                            }

                            override fun onSuccess(imagePath: String) {
                                addInfoImgList[index] = imagePath
                            }
                        })
                }
            }.await()
            Log.e("저장된 이미지", addInfoImgList.toString())

            uploadImgList()
        }
    }

    private fun uploadImgList() {
        loadingDialog = LoadingDialog(this)
        val bookIndex = MyApplication.prefs.getBookIndex("bookIndex", 0)

        loadingDialog.show()
        addInfoImgList.forEachIndexed{i, imgPath ->
            // StorageReference 에 파일 업로드
            // uid/books/book$index/book에 사용되는 Img 및 정보들
            val fileRef: StorageReference =
                storageRef.child("$userRef").child("book$bookIndex"
                )

            // 2. StorageReference 에 업로드한 파일 -> 실시간 DB에 uid 아래에 정보 저장
            fileRef.putFile(Uri.fromFile(File(imgPath))).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri -> // 이미지 모델에 담기
                    /*val imgModel = ImgDto(selectedImgs[i].imgInfo!!, uri.toString())

                    //키로 아이디 생성
                    val modelId: String = userRef.push().key!!

                    //데이터 넣기
                    // user가 생성했던 책 개수를 새는 SharedPref 의 Int 해당 index 적용
                    userRef.child("book$bookIndex").child(modelId).setValue(imgModel)*/

                    //프로그래스바 숨김
                    Toast.makeText(this@EditImgActivity, "업로드 성공", Toast.LENGTH_SHORT).show()
                    Log.e("업로드 성공", "성공함")
                    MyApplication.prefs.setBookIndex("bookIndex", bookIndex+1)
                    if(i==addInfoImgList.lastIndex){
                        loadingDialog.dismiss()
                        goMainActivity()
                    }
                }
            }.addOnProgressListener { // 로딩애니메이션 표시
            }.addOnFailureListener {
                Toast.makeText(this@EditImgActivity, "업로드 실패", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
            Log.e("업로드 작업", "완료함")
            // 메인액티비티 의 BookFragment 보여주기
        }
    }

    //파일타입 가져오기
    private fun getFileExtension(uri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun goMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("sentActivity", "EditImgActivity")
        startActivity(intent)
        finish()
    }

}