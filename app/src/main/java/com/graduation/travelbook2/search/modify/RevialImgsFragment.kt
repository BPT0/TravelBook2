package com.graduation.travelbook2.search.modify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.graduation.travelbook2.R
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.databinding.FragmentRevialImgsBinding
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import com.graduation.travelbook2.search.SearchFragment
import com.graduation.travelbook2.search.adapter.SelectedImgAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.pipecodingclub.travelbook.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 대표 사진으로 선택될 화면을 보여주는 프레그먼트
 */
class RevialImgsFragment : BaseFragment<FragmentRevialImgsBinding>(FragmentRevialImgsBinding::inflate){
    companion object{
        const val tag: String = "대표이미지 설정 프레그먼트"
        fun newInstance(): RevialImgsFragment {
            return RevialImgsFragment()
        }

    }

    /* db 관련 변수 */
    private lateinit var db : ImgInfoDb   // 이미지 정보 db 객체

    /* 선택된 사진들을 보여주는 RV*/
    private lateinit var selectedImgAdapter: SelectedImgAdapter
    private val selectedImg: ArrayList<SelectedImgDto> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = ImgInfoDb.getInstance(this.requireContext())!!


    }

}