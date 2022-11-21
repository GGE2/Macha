package com.example.final_pjt.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_pjt.dto.RoomDetail

private const val TAG = "RoomViewModel_싸피"
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
    fun removeRoom(id : String){
        for(i:Int in 0 until data.size){
            Log.d(TAG, "removeRoom: ${data[i].toString()}")
            if(data[i].roomId.equals(id)){
                data.removeAt(i)
            }
        }
        roomLiveData.value = data
    }


}