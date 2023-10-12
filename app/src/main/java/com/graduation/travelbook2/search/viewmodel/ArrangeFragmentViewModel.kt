package com.graduation.travelbook2.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.travelbook2.search.dto.ArrageFragmentDto

class ArrangeFragmentViewModel: ViewModel() {
    private val _arrangeFragmentModel = MutableLiveData(ArrageFragmentDto(false, false))
    val petPurchaseModel: LiveData<ArrageFragmentDto>
        get() = _arrangeFragmentModel

}