package com.pipecodingclub.travelbook.share

import android.os.Bundle
import android.view.View
import com.graduation.travelbook2.databinding.FragmentShareBinding
import com.pipecodingclub.travelbook.base.BaseFragment

class ShareFragment : BaseFragment<FragmentShareBinding>(FragmentShareBinding::inflate){
    companion object{
        const val tag: String = "공유탭"
        fun newInstance(): ShareFragment {
            return ShareFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}