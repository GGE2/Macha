package com.example.final_pjt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.dto.Room

class RoomAdapter(var rooms : List<Room>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>(){
    class RoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

}