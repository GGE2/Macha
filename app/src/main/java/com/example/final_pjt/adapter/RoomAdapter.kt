package com.example.final_pjt.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.databinding.ItemRoomBinding
import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail

class RoomAdapter(var rooms : MutableList<RoomDetail>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>(){
    private lateinit var binding : ItemRoomBinding
    private lateinit var roomClickListener:OnRoomClickListener
    inner class RoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindData(room:RoomDetail){
            binding.roomItemTvTitle.text = room.roomName.toString()
            binding.roomItemTvPerson.text = "${room.numOfPeople}/${room.maxNumOfPeople}"
            binding.roomItemBtnSetting.text = "입장순"
            binding.roomItemBtnEnter.setOnClickListener {
                roomClickListener.onRoomClickListener(it,layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RoomHolder(binding.root)
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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: RoomHolder) {
        super.onViewRecycled(holder)
    }
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