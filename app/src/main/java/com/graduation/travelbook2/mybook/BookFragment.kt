package com.graduation.travelbook2.mybook

import android.os.Bundle
import android.view.View
import com.graduation.travelbook2.databinding.FragmentBookBinding
import com.pipecodingclub.travelbook.base.BaseFragment

class BookFragment : BaseFragment<FragmentBookBinding>(FragmentBookBinding::inflate){
    companion object{
        const val tag: String = "책만들기탭"
        fun newInstance(): BookFragment {
            return BookFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}