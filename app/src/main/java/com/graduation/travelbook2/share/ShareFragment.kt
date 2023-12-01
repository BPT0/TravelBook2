package com.graduation.travelbook2.share

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
                        val job = Job()
                        val uiScope = CoroutineScope(Dispatchers.Main + job)
                        uiScope.launch {
                            for (i in 1..index) {
                                val temp = ArrayList<Uri>()
                                val images = fetchImagesFromFirebase(i)
                                temp.addAll(images)
                                updateRecyclerView(i, temp)
                            }
                        }
                    }
                }
            })
            loadingDialog.dismiss()
        }
    }

    suspend fun fetchImagesFromFirebase(bookNumber: Int): List<Uri> {
        return withContext(Dispatchers.IO) { // IO 스레드에서 네트워크 요청을 수행합니다.
            val listResult = shareBooksRef.child("books$bookNumber").listAll().await() // await 함수를 사용하여 비동기 작업을 동기적으로 대기합니다.
            listResult.items.map { it.downloadUrl.await() } // 각 이미지의 URL을 다운로드합니다.
        }
    }

    fun updateRecyclerView(bookNumber: Int, images: ArrayList<Uri>) {
        mapBook[bookNumber - 1] = images
        diaryListAdapter.addImgDiary(bookNumber - 1, mapBook[bookNumber - 1])
        diaryListAdapter.notifyItemChanged(bookNumber - 1)
    }

    private fun setBookRCView() {
        // 공유 이미지 탭에 추가된 책이 있다면
        binding.rvMyBooks.apply {
            // todo. sample data 추가
            val imageResource = R.drawable.img_add_info
            val uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(imageResource))
                .appendPath(resources.getResourceTypeName(imageResource))
                .appendPath(resources.getResourceEntryName(imageResource))
                .build()
            mapBook[0] = ArrayList()
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