package com.example.final_pjt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_pjt.dto.RoomDetail

class RoomViewModel : ViewModel() {

    val roomLiveData = MutableLiveData<MutableList<RoomDetail>>()

    private var data = mutableListOf<RoomDetail>()

    fun setData(list:MutableList<RoomDetail>){
        data = list
        roomLiveData.value = data
    }
    fun addRoom(room:RoomDetail){
        data.add(0,room)
        roomLiveData.value = data
    }
    fun removeRoom(index:Int){
        data.removeAt(index)
        roomLiveData.value = data
    }

}