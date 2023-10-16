package com.graduation.travelbook2.search.modify

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.FragmentAddInfoBinding
import com.pipecodingclub.travelbook.base.BaseFragment

/**
 *
 */
class AddInfoFragment : BaseFragment<FragmentAddInfoBinding>(FragmentAddInfoBinding::inflate) {
    companion object {
        const val tag: String = "대표이미지 설정 프레그먼트"
        fun newInstance(): AddInfoFragment {
            return AddInfoFragment()
        }
    }

    private lateinit var imgInfo: ImgInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {
            // 이미지뷰는 fitCenter로 보여줌
            Glide.with(this.root)
                .load(imgInfo.path)
                .into(ivImg)

        }
    }
}




