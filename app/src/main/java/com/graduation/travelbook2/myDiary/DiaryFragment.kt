package com.graduation.travelbook2.myDiary

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.databinding.FragmentBookBinding
import com.graduation.travelbook2.loading.LoadingDialog
import com.graduation.travelbook2.myDiary.adapter.DiaryListAdapter
import com.pipecodingclub.travelbook.base.BaseFragment
import com.graduation.travelbook2.adapterDeco.GridItemDeco
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DiaryFragment : BaseFragment<FragmentBookBinding>(FragmentBookBinding::inflate){
    companion object{
        const val tag: String = "책 만들기 탭"
        fun newInstance(): DiaryFragment {
            return DiaryFragment()
        }
    }

    private val storageBookRef = FirebaseStorage.getInstance().reference.child("allImages")

    private lateinit var diaryListAdapter: DiaryListAdapter
    private var mapBook: MutableMap<Int, ArrayList<Uri>> = mutableMapOf()

    private lateinit var loadingDialog : LoadingDialog
    private var createdBooks: Int =0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadBook()
        setSwipeRefresh()

    }

    private fun downloadBook() {
        // allImages 폴더 내부에 파일이 있다면
        createdBooks = MyApplication.prefs.getBookIndex("bookIndex", 0)

        CoroutineScope(Dispatchers.Main).launch {
            if (createdBooks > 0){
                loadingDialog = LoadingDialog(this@DiaryFragment.requireContext())
                loadingDialog.show()
                for(i in 0 until createdBooks){
                    storageBookRef.child("book$i").listAll().addOnSuccessListener {
                        mapBook[i] = ArrayList()
                        CoroutineScope(Dispatchers.Main).launch {
                            it.items.forEach { item ->
                                item.downloadUrl.addOnSuccessListener { uri ->
                                    mapBook[i]!!.add(uri)
                                }.addOnFailureListener {
                                    loadingDialog.dismiss()
                                }.await()
                                if (i == createdBooks - 1) {
                                    Log.e("다운로드한 이미지들", mapBook.toString())
                                    loadingDialog.dismiss()
                                    setBookRCView()
                                }
                            }
                        }
                    }.await()
                }
            }
        }
    }

    private fun setBookRCView() {
        binding.rvMyBooks.apply {
            diaryListAdapter = DiaryListAdapter(this@DiaryFragment.requireContext(), mapBook)
            adapter = diaryListAdapter
            addItemDecoration(GridItemDeco(3, 5f.fromDpToPx()))
        }
    }

    private fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

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
        val newBookIndex = MyApplication.prefs.getBookIndex("bookIndex", 0)
        val hasNewBook = (createdBooks != newBookIndex)
        if (createdBooks!=0 && hasNewBook){
            CoroutineScope(Dispatchers.Main).launch {
                for (i in createdBooks until newBookIndex){
                    storageBookRef.child("book$i").listAll().addOnSuccessListener {
                        mapBook[i] = ArrayList()
                        CoroutineScope(Dispatchers.Main).launch {
                            it.items.forEach { item ->
                                item.downloadUrl.addOnSuccessListener { uri ->
                                    mapBook[i]!!.add(uri)
                                    if (i == createdBooks - 1) {
                                        Log.e("다운로드한 이미지들", mapBook.toString())
                                        loadingDialog.dismiss()
                                        updateBookRCView(newBookIndex)
                                    }
                                }.addOnFailureListener {
                                    loadingDialog.dismiss()
                                }.await()
                            }
                        }
                    }.await()
                }
            }
        }

        // 스테그리드 RCview 표시
        // - 각 뷰마다 보이는 내용 표지의 첫번째 이미지
    }

    private fun updateBookRCView(addPosition: Int) {
        binding.rvMyBooks.apply {
            diaryListAdapter.addImgDiary(mapBook)
            diaryListAdapter.notifyItemInserted(addPosition)
        }
    }
}