package com.graduation.travelbook2.search.modify

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.FragmentAddInfoBinding
import com.pipecodingclub.travelbook.base.BaseFragment
import ja.burhanrashid52.photoeditor.PhotoEditor

/**
 * 이미지 정보추가 프레그먼트
 */
class AddInfoFragment : BaseFragment<FragmentAddInfoBinding>(FragmentAddInfoBinding::inflate) {
    companion object {
        const val tag: String = "이미지 정보추가 프레그먼트"
        fun newInstance(): AddInfoFragment {
            return AddInfoFragment()
        }
    }

    private lateinit var imgInfo: ImgInfo

    private lateinit var mPhotoEditor : PhotoEditor
    private var isLastFragment = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // savedInstanceState 번들을 사용하여 이전에 저장한 데이터나 상태를 복원 & 뷰를 생성

        imgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {
            // 이미지뷰는 fitCenter로 보여주기
            photoEditorView.source.setImageURI(imgInfo.path!!.toUri())

            mPhotoEditor = PhotoEditor.Builder(this@AddInfoFragment.requireContext(), photoEditorView)
                .setPinchTextScalable(true)
                .setClipSourceImage(true)
                .build()

            btnAddText.setOnClickListener {
                mPhotoEditor.addText(etxAddText.text.toString(), Color.BLACK)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 필요한 데이터나 상태를 outState 번들에 저장
    }
}




