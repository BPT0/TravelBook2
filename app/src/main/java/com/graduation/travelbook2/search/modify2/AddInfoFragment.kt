package com.graduation.travelbook2.search.modify2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.FragmentAddInfoBinding
import com.graduation.travelbook2.internalDto.AddInfoImgDto
import com.graduation.travelbook2.search.dialog.DialogSelectColor
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

    lateinit var mPhotoEditor: PhotoEditor

    var bitmapImg: AddInfoImgDto? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // savedInstanceState 번들을 사용하여 이전에 저장한 데이터나 상태를 복원 & 뷰를 생성

        imgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {
            // 이미지뷰는 fitCenter로 보여주기
            photoEditorView.source.setImageURI(imgInfo.path!!.toUri())

            mPhotoEditor =
                PhotoEditor.Builder(this@AddInfoFragment.requireContext(), photoEditorView)
                    .setPinchTextScalable(true).setClipSourceImage(true).build()

            // 완료버튼 누를시 실행 텍스트 추가하게 하기
            etxAddText.setOnEditorActionListener(getEditorActionListener(btnAddText))
            btnAddText.setOnClickListener {
                // todo: 색상 선택 다이얼로그 표시

                val dialog = DialogSelectColor(this@AddInfoFragment.requireContext())
                dialog.show()

                dialog.setItemClickListener(object : DialogSelectColor.ItemClickListener{
                    override fun onClick(color: Int) {

                        Log.e("색상", "$color")
                        mPhotoEditor.addText(etxAddText.text.toString(), color)
                    }
                })

            }
        }
    }

    fun getEditorActionListener(view: View): TextView.OnEditorActionListener { // 키보드에서 done(완료) 클릭 시 , 원하는 뷰 클릭되게 하는 메소드
        return TextView.OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.callOnClick()
            }
            false
        }
    }

}




