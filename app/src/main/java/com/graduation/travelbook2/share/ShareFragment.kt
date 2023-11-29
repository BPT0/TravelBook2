package com.graduation.travelbook2.share

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.graduation.travelbook2.R
import com.graduation.travelbook2.adapterDeco.GridItemDeco
import com.graduation.travelbook2.databinding.FragmentShareBinding
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.share.adapter.DiaryListAdapter
import com.pipecodingclub.travelbook.base.BaseFragment
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareFragment : BaseFragment<FragmentShareBinding>(FragmentShareBinding::inflate){
    companion object{
        const val tag: String = "공유 탭"
        fun newInstance(): ShareFragment {
            return ShareFragment()
        }
    }

    // DB 관련 변수
    private val database = Firebase.database  // book들의 index를 관리하기 위해 Firebase Database 사용
    private val shareIndexRef = database.getReference("TravelBook2").child("shareBookIndex")
    private val shareBooksRef = FirebaseStorage.getInstance().reference
        .child("allBooks")
        .child("shareBooks")

    private lateinit var diaryListAdapter: DiaryListAdapter
    private var mapBook: MutableMap<Int, ArrayList<Uri>> = mutableMapOf()

    private lateinit var loadingDialog : LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBookRCView()
        downloadBook()
        setSwipeRefresh()
    }

    private fun downloadBook() {
        // todo: 추가된 이미지만 다운로드하고
        //   1번째 book 일때 : RV 새로 setting
        //   2번째 book 부터 : RV에 item추가 될수 있도록 수정
        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog = LoadingDialog(this@ShareFragment.requireContext())
            loadingDialog.show()
            // 저장된 사진URL을 가져와서 리스트에 추가
            withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
                // index 설정
                shareIndexRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            val index = currentData?.getValue(Int::class.java) ?: 0
                            if(index <= 0) // todo: 공유된 사진이 없다는 안내 표시
                                return
                            for(i: Int in 1 .. index){
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(CoroutineScope(Dispatchers.Main).coroutineContext){
                                        processBook(i)
                                    }
                                }
                            }
                        }
                    }
                })
            }
            loadingDialog.dismiss()
        }
    }

    private suspend fun processBook(i: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            shareBooksRef.child("books${i}").listAll().addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val temp = ArrayList<Uri>()
                    val downImgsTask = it.items.map { img ->
                        withContext(Dispatchers.Main) {
                            val deferredDown = CompletableDeferred<Uri>()
                            img.downloadUrl
                                .addOnSuccessListener { uri ->
                                    temp.add(uri)
                                    deferredDown.complete(uri)
                                }
                                .addOnFailureListener { exception ->
                                    loadingDialog.dismiss()
                                    deferredDown.completeExceptionally(exception)
                                }
                            deferredDown.await()
                        }
                        mapBook[i-1] = temp
                        diaryListAdapter.addImgDiary(i-1, mapBook[i-1])
                    }
                    diaryListAdapter.notifyItemChanged(i-1)
                }
            }
        }

    }

    private fun setBookRCView() {
        // 공유 이미지 탭에 추가된 책이 있다면
        binding.rvMyBooks.apply {
            // todo. sample data 추가
            val imageResource = R.drawable.sample_image
            val uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(imageResource))
                .appendPath(resources.getResourceTypeName(imageResource))
                .appendPath(resources.getResourceEntryName(imageResource))
                .build()
            mapBook[0] = ArrayList<Uri>()
            mapBook[0]?.add(uri)

            diaryListAdapter = DiaryListAdapter(this@ShareFragment.requireContext(), mapBook)
            adapter = diaryListAdapter
            addItemDecoration(GridItemDeco(3, 5f.fromDpToPx()))
        }
    }

    private fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()


    private fun refreshBookRCView(){
        diaryListAdapter.notifyItemRangeChanged(0, mapBook.size-1)
    }

    private fun setSwipeRefresh() {
        binding.apply {
            layoutSwipeRefresh.setOnRefreshListener {
                refreshData()
                layoutSwipeRefresh.isRefreshing = false
            }
            // 스크롤 업 대신에 리프레쉬 이벤트가 트리거 되는걸 방지
            layoutScroll.viewTreeObserver.addOnScrollChangedListener {
                layoutSwipeRefresh.isEnabled = (layoutScroll.scrollY == 0)
            }
        }
    }



    private fun refreshData() {
        downloadBook()
    }

}