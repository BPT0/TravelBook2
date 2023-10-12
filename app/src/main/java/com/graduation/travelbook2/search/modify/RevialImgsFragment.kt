package com.graduation.travelbook2.search.modify

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.databinding.FragmentRevialImgsBinding
import com.graduation.travelbook2.search.listenerNcallback.OnCheckboxChangedListener
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

    private lateinit var imgInfo: ImgInfo

    private var listener: OnCheckboxChangedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCheckboxChangedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {
            // 이미지뷰는 fitCenter로 보여줌
            Glide.with(this.root)
                .load(imgInfo.path)
                .into(ivImg)

            cbxTitle.setOnCheckedChangeListener { _, isChecked ->
                listener?.onCheckboxChanged(isChecked)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        imgInfo = arguments?.getParcelable("imgInfo")!!

        binding.apply {
            Glide.with(this.root)
                .load(imgInfo.path)
                .into(ivImg)
        }
    }

    fun updateBtnEnabled(enabled: Boolean){
        binding.btnImgAddInfo.isEnabled = enabled
    }


    fun updateCbtnEnabled(enabled: Boolean){
        binding.cbxTitle.isEnabled = enabled
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}