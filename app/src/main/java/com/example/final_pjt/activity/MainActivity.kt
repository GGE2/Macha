package com.example.final_pjt.activity

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.R
import com.example.final_pjt.adapter.RoomAdapter
import com.example.final_pjt.databinding.ActivityMainBinding
import com.example.final_pjt.dto.Room

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var roomAdapter: RoomAdapter
    var list = mutableListOf<Room>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        temp()
        setAdapter()
    }
    private fun setAdapter(){
        roomAdapter = RoomAdapter(list)
        binding.recyclerMainRoom.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalSpaceItemDecoration(20))
        }
    }

    private fun temp(){
        list.add(Room("1","아무나 들어와라",2))
        list.add(Room("2","한번 붙자",1))
        list.add(Room("3","내 그림 맞춰볼래?",1))
        list.add(Room("2","내 그림 맞히면 천재!",1))
    }

    //RecyclerView 간격 조절
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