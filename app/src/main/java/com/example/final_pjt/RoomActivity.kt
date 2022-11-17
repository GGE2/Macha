package com.example.final_pjt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.final_pjt.databinding.ActivityRoomBinding

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}