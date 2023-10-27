package com.graduation.travelbook2.search.modify

import android.R
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.ActivityEditImgBinding
import com.graduation.travelbook2.dto.ImgDto
import com.graduation.travelbook2.loading.CircleProgressDialog
import com.graduation.travelbook2.search.adapter.ImgSelectedAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
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

    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private val reference = FirebaseStorage.getInstance().reference

    private val loadingDialog = CircleProgressDialog()

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


            btnMakeDiary.apply {
                setOnClickListener {
                    saveEditImg()
                }
            }
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
        addInfoImgList.forEach{ imgPath ->
            val fileRef: StorageReference =
                reference.child(System.currentTimeMillis().toString() + "."
                        + getFileExtension(Uri.fromFile(File(imgPath!!)))
                )

            fileRef.putFile(Uri.fromFile(File(imgPath))).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri -> //이미지 모델에 담기
                    val imgModel = ImgDto(uri.toString())

                    //키로 아이디 생성
                    val modelId: String = root.push().key!!

                    //데이터 넣기
                    root.child(modelId).setValue(imgModel)

                    //프로그래스바 숨김
                    Toast.makeText(this@EditImgActivity, "업로드 성공", Toast.LENGTH_SHORT).show()
                    // loadingDialog.dismiss()
                }
            }.addOnProgressListener { //프로그래스바 보여주기
                // loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            }.addOnFailureListener { //프로그래스바 숨김
                Toast.makeText(this@EditImgActivity, "업로드 실패", Toast.LENGTH_SHORT).show()
                // loadingDialog.dismiss()
            }
        }
    }

    //파일타입 가져오기
    private fun getFileExtension(uri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun saveDefaultImgs() {
        selectedImgs.forEach {img ->
            val filePath = img.imgInfo?.path
            addInfoImgList.add(filePath)
            Log.e("저장된 이미지", addInfoImgList.size.toString())
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

    private fun createImgFragments() {
        for (i: Int in 0 until selectedImgs.size){
            // bundle로 프레그먼트에 필요한 리스트의 정보 전달
            listAddInfoImgFragment.add(AddInfoFragment.newInstance())
            val bundle= Bundle()
            bundle.putSerializable("imgInfo", selectedImgs[i].imgInfo)
            listAddInfoImgFragment[i].arguments = bundle
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


}