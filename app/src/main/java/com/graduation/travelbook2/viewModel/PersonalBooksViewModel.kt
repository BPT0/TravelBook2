package com.graduation.travelbook2.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.graduation.travelbook2.repo.PersnoalBookRepo

class PersonalBooksViewModel: ViewModel() {
    private val bookImgDtos = PersnoalBookRepo()
    // private val bookImgFirstList: LiveData<BookImgFirstList> = bookImgDtos.listBook
    // private val bookImgs: LiveData<BookImgInfo> = bookImgDtos.book


}