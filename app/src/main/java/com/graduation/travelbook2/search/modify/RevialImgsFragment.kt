package com.graduation.travelbook2.search.modify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.graduation.travelbook2.R
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.databinding.FragmentRevialImgsBinding
import com.graduation.travelbook2.databinding.FragmentSearchBinding
import com.graduation.travelbook2.search.SearchFragment
import com.graduation.travelbook2.search.adapter.SelectedImgAdapter
import com.graduation.travelbook2.search.dto.SelectedImgDto
import com.pipecodingclub.travelbook.base.BaseFragment

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

    private lateinit var getResultBoolean : ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = ImgInfoDb.getInstance(this.requireContext())!!

        val imgInfo: ImgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {

            // 이미지뷰는 fitCenter로 보여줌
            Glide.with(this.root)
                .load(imgInfo.path)
                .into(ivImg)
        }

        getResultBoolean = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            result->
            // 엑티비티에서 처리한 isChecked의 값을 다시 가져옴
            val isChecked = result.data?.getBooleanExtra("data", false)
        }

        // 체크박스 체크여부 설정상태 액티비티에 전달
        val cintent = Intent(this.requireContext(), ArrangeImgsOrderActivity::class.java)
        cintent.apply {
            putExtra("isTitleChecked", binding.rbtnTitle.isChecked)
        }
        getResultBoolean.launch(cintent)


    }

}