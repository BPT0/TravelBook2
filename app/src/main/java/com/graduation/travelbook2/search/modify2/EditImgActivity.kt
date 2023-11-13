package com.graduation.travelbook2.search.modify2

import android.content.Intent
import android.graphics.Bitmap
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
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.SaveSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File


class EditImgActivity : BaseActivity<ActivityEditImgBinding>() {
    override val TAG : String = EditImgActivity::class.java.simpleName
    override val layoutRes: Int = com.graduation.travelbook2.R.layout.activity_edit_img

    private lateinit var selectedImgs: ArrayList<SelectedImgDto>

    private val listAddInfoImgFragment : ArrayList<AddInfoFragment> = ArrayList() // 프레그먼트 배열

    private lateinit var imgSelectAdapter: ImgSelectedAdapter // 어답터

    private val listBitmapFile : ArrayList<Bitmap?> = ArrayList()

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
        selectedImgs.forEach{ img ->
            listBitmapFile.add(null)
        }
    }

    private fun saveEditImg() {
        // 모든 프레그먼트들의 이미지들을 저장함
        CoroutineScope(Dispatchers.Main).launch {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                val saveSettings = SaveSettings.Builder()
                    .setClearViewsEnabled(false)
                    .build()
                listAddInfoImgFragment.forEachIndexed { index, fragment ->
                    fragment.mPhotoEditor.saveAsBitmap(saveSettings, object : OnSaveBitmap{
                        override fun onBitmapReady(saveBitmap: Bitmap?) {
                            saveBitmap!!.let {
                                Log.e("프레그먼트의 이미지 저장","Image Saved Successfully")
                                listBitmapFile[index] = saveBitmap
                            }
                        }

                        override fun onFailure(e: Exception?) {
                            Log.e("프래그먼트 이미지 저장","Failed to save Image: $e")
                        }

                    })
                }
            }

            upLoadFromMemoryImgList(listBitmapFile)
        }
    }

    private fun upLoadFromMemoryImgList(listBitmap: ArrayList<Bitmap?>){

        Log.e("업로드 이미지 파일", listBitmap.toString())
        loadingDialog = LoadingDialog(this)
        val bookIndex = MyApplication.prefs.getBookIndex("bookIndex", 0)
        loadingDialog.show()

        listBitmap.forEachIndexed{i, bitmap ->
            val baos =ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
            val data=baos.toByteArray()

            storageBookRef.child("book$bookIndex").child("img$i.png")
                .putBytes(data).addOnCompleteListener {
                    if(i == listBitmapFile.lastIndex){
                        MyApplication.prefs.setBookIndex("bookIndex", bookIndex+1)
                        Toast.makeText(this@EditImgActivity, "업로드 완료", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                        goMainActivity()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this@EditImgActivity, "업로드 실패", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
        }

    }

    private fun goMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("sentActivity", "EditImgActivity")
        startActivity(intent)
        finish()
    }

}