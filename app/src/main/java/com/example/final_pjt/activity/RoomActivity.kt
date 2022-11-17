package com.example.final_pjt.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.final_pjt.adapter.ChatAdapter
import com.example.final_pjt.databinding.ActivityRoomBinding
import com.example.final_pjt.databinding.DialogGameEndBinding
import com.example.final_pjt.dto.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent

private const val TAG = "RoomActivity_싸피"

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    private var list: MutableList<Message> = mutableListOf()
    private val url = "ws://192.168.0.4:8080/stomp/chat/websocket"
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
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
        binding.roomChatRecyclerView.adapter = ChatAdapter(listOf())
        binding.roomChatRecyclerView.layoutManager = LinearLayoutManager(this)
        stompClient.topic("/sub/chat/room/77f1be64-aecc-44a0-8f5b-46eac642fa20").subscribe{
            topicMessage ->
            Log.d(TAG, "onCreate: ${topicMessage.payload}")
            val message = Gson().fromJson(topicMessage.payload, Message::class.java)
            list.add(message)
            runOnUiThread {
                binding.roomChatRecyclerView.adapter = ChatAdapter(list.toList())
            }
        }
        stompClient.connect()
        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.i("OPEND", "!!")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.i("CLOSED", "!!")

                }
                LifecycleEvent.Type.ERROR -> {
                    Log.i("ERROR", "!!")
                    Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
                }
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }
        binding.roomChatSendButton.setOnClickListener {
            val data = JSONObject()
            data.put("message", binding.roomChatEditText.text.toString())
            data.put("writer", auth.currentUser?.uid)
            data.put("roomId", "77f1be64-aecc-44a0-8f5b-46eac642fa20")
            stompClient.send("/pub/chat/message", data.toString()).subscribe()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }
}