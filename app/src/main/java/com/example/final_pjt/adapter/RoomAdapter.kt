package com.example.final_pjt.adapter

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.R
import com.example.final_pjt.databinding.ItemRoomBinding
import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail
import com.example.final_pjt.dto.RoomStatusEnum

private const val TAG = "RoomAdapter_싸피"
class RoomAdapter(var rooms : MutableList<RoomDetail>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>(){
    private lateinit var roomClickListener: OnRoomClickListener
    inner class RoomHolder(var binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(room:RoomDetail){
            if(room.roomStatus==RoomStatusEnum.PROCEEDING){
                binding.roomItemBtnEnter.isClickable = false
                binding.roomItemBtnSetting.text = "게임중"
                binding.roomItemBack.setBackgroundResource(R.drawable.background_mint_radius_no)
                binding.roomItemTvTitle.text = room.roomName.toString()
                binding.roomItemTvPerson.text = "${room.numOfPeople} / ${room.maxNumOfPeople}"
            }
            else if(room.numOfPeople>=room.maxNumOfPeople){
                binding.roomItemBtnEnter.isClickable = false
                binding.roomItemBtnSetting.text = "인원 초과"
                binding.roomItemBack.setBackgroundResource(R.drawable.background_mint_radius_no)
                binding.roomItemTvTitle.text = room.roomName.toString()
                binding.roomItemTvPerson.text = "${room.numOfPeople} / ${room.maxNumOfPeople}"
            }
            else{
                binding.roomItemBtnEnter.isClickable = true
                binding.roomItemBack.setBackgroundResource(R.drawable.background_mint_radius)
                binding.roomItemBtnSetting.text = "게임 대기"
                binding.roomItemTvTitle.text = room.roomName.toString()
                binding.roomItemTvPerson.text = "${room.numOfPeople} / ${room.maxNumOfPeople}"
                binding.roomItemBtnEnter.setOnClickListener {
                    roomClickListener.onRoomClickListener(it,layoutPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RoomHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        holder.bindData(rooms[position])
    }

    override fun getItemCount(): Int {
        return rooms.size
    }
    interface OnRoomClickListener{
        fun onRoomClickListener(view:View,position: Int)
    }
    fun setOnRoomClickListener(roomClickListener: OnRoomClickListener){
        this.roomClickListener = roomClickListener
    }

   /* override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: RoomHolder) {
        super.onViewRecycled(holder)
    }*/
    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight:Int):
            RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
            }

}