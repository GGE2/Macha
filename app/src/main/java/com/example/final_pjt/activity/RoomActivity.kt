package com.example.final_pjt.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.final_pjt.adapter.ChatAdapter
import com.example.final_pjt.databinding.ActivityRoomBinding
import com.example.final_pjt.databinding.DialogGameEndBinding
import com.example.final_pjt.dto.Message
import com.google.firebase.auth.FirebaseAuth

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
        val auth = FirebaseAuth.getInstance()
        binding.roomChatRecyclerView.adapter = ChatAdapter(listOf(Message(auth.currentUser?.uid ?: "", "테스트"), Message("other", "상대 테스트")))
        binding.roomChatRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}