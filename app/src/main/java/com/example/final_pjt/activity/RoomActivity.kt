package com.example.final_pjt.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.final_pjt.databinding.ActivityRoomBinding
import com.example.final_pjt.databinding.DialogGameEndBinding

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.roomStartButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogBinding = DialogGameEndBinding.inflate(builder.create().layoutInflater)
            builder.setView(dialogBinding.root)
            builder.setPositiveButton("확인") { dialog, _ -> dialog?.cancel() }.show()
        }
    }
}