package com.graduation.travelbook2.mybook

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.databinding.FragmentBookBinding
import com.pipecodingclub.travelbook.base.BaseFragment

class BookFragment : BaseFragment<FragmentBookBinding>(FragmentBookBinding::inflate){
    companion object{
        const val tag: String = "책 만들기 탭"
        fun newInstance(): BookFragment {
            return BookFragment()
        }
    }

    private var storageRef = FirebaseStorage.getInstance().reference.child("TravelBook2")
        .child("UserAccount")
    private val auth = FirebaseAuth.getInstance() // 유저 만들기

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            layoutSwipeRefresh.setOnRefreshListener {
                refreshData()
            }
            // 스크롤 업 대신에 리프레쉬 이벤트가 트리거 되는걸 방지
            layoutScroll.viewTreeObserver.addOnScrollChangedListener {
                layoutSwipeRefresh.isEnabled = (layoutScroll.scrollY == 0)
            }
        }


    }

    private fun refreshData() {
        // if - viewmodel observer 적용

        // 파이어베이스 DB에서 User에 book이 있다면 가져와서
        storageRef = storageRef.child(auth.currentUser?.uid!!)
        val createdBooks = MyApplication.prefs.getBookIndex("bookIndex", 0)
        for (i in 0 until createdBooks){
            storageRef.child("book$i").downloadUrl.addOnSuccessListener { url->
                Log.e("다운로드한 이미지들", url.toString())
            }.addOnFailureListener {

            }.addOnCompleteListener {

            }
        }

        // 스테그리드 RCview 표시
        // - 각 뷰마다 보이는 내용 표지의 첫번째 이미지
    }
}