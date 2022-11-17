package com.example.final_pjt.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.databinding.ItemRoomBinding
import com.example.final_pjt.dto.Room

class RoomAdapter(var rooms : List<Room>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>(){
    private lateinit var binding : ItemRoomBinding

    inner class RoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindData(room:Room){
            binding.roomItemTvTitle.text = room.roomTitle
            binding.roomItemTvPerson.text = "${room.roomPersonnel}/4"
            binding.roomItemBtnSetting.text = "입장순"
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