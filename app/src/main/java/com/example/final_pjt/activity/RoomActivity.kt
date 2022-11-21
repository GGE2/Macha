package com.example.final_pjt.activity

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.final_pjt.adapter.ChatAdapter
import com.example.final_pjt.databinding.ActivityRoomBinding
import com.example.final_pjt.databinding.DialogGameEndBinding
import com.example.final_pjt.dto.Message
import com.example.final_pjt.dto.PointWithRoomId
import com.example.final_pjt.dto.RoomDetail
import com.example.final_pjt.dto.User
import com.example.final_pjt.service.RoomService
import com.example.final_pjt.util.ApplicationClass
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject
import org.json.JSONStringer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import com.example.final_pjt.util.ApplicationClass.Companion.sharedPreferencesUtil

private const val TAG = "RoomActivity_싸피"

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    private var list: MutableList<Message> = mutableListOf()
    private val url = "ws://54.180.24.155:8080/stomp/game/websocket"
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
    private var roomDetail: RoomDetail? = null
    var roomId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomId = intent.getStringExtra("roomId")
        Log.d(TAG, "onCreate: ${roomId}")
        binding.draw.roomId = roomId!!
        binding.roomStartButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogBinding = DialogGameEndBinding.inflate(builder.create().layoutInflater)
            builder.setView(dialogBinding.root)
            builder.setPositiveButton("확인") { dialog, _ -> dialog?.cancel() }.show()
        }
        val auth = FirebaseAuth.getInstance()
        binding.roomChatRecyclerView.adapter = ChatAdapter(listOf())
        binding.roomChatRecyclerView.layoutManager = LinearLayoutManager(this)
        
        stompClient.connect()

        val data = JSONObject()
        data.put("roomId", roomId);
        val userJson = JSONObject()
        val user = sharedPreferencesUtil.getUser()
        userJson.put("userToken", auth.currentUser?.uid)
        userJson.put("nickname", auth.currentUser?.displayName!!)
        userJson.put("profileImg", auth.currentUser?.photoUrl!!.toString())
        userJson.put("userId", user.userId)
        userJson.put("isOnline", 1)
        data.put("user", userJson)
        data.put("roomId", "${roomId}")

        stompClient.topic("/sub/chat/room/${roomId}").subscribe{
                topicMessage ->
            Log.d(TAG, "onCreate: ${topicMessage.payload}")
            val message = Gson().fromJson(topicMessage.payload, Message::class.java)
            list.add(message)
            runOnUiThread {
                binding.roomChatRecyclerView.adapter = ChatAdapter(list.toList())
            }
        }

        stompClient.topic("/sub/game/room/${roomId}").subscribe{
            topicMessage ->
            roomDetail = Gson().fromJson(topicMessage.payload, RoomDetail::class.java)
            runOnUiThread {
                binding.draw.nowDrawer = roomDetail!!.nowDrawer == auth.currentUser?.uid
                if(roomDetail!!.nowDrawer == auth.currentUser?.uid){
                    binding.drawClearAll.visibility = View.VISIBLE
                    binding.drawPencil.visibility = View.VISIBLE
                    binding.drawEraser.visibility = View.VISIBLE
                } else {
                    binding.drawClearAll.visibility = View.GONE
                    binding.drawPencil.visibility = View.GONE
                    binding.drawEraser.visibility = View.GONE
                }
            }
            Log.d(TAG, "onCreate: ${topicMessage.payload}")
        }

        stompClient.topic("/sub/canvas/room/${roomId}").subscribe{
            topicMessage ->
            runOnUiThread {
                binding.draw.addPoint(Gson().fromJson(topicMessage.payload, PointWithRoomId::class.java))
            }
        }

        stompClient.topic("/sub/canvas/clear/${roomId}").subscribe{
                topicMessage ->
            runOnUiThread {
                binding.draw.clear()
            }
        }

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
        stompClient.send("/pub/game/enter", data.toString()).subscribe()
        stompClient.send("/pub/chat/enter", data.toString()).subscribe()
        binding.roomChatSendButton.setOnClickListener {
            val data = JSONObject()
            data.put("message", binding.roomChatEditText.text.toString())
            val userJson = JSONObject()
            userJson.put("userToken", auth.currentUser?.uid)
            userJson.put("nickname", auth?.currentUser?.displayName!!)
            userJson.put("profileImg", auth.currentUser?.photoUrl!!.toString())
            userJson.put("userId", -1)
            userJson.put("isOnline", 1)
            data.put("user", userJson)
            data.put("roomId", "${roomId}")
            stompClient.send("/pub/chat/message", data.toString()).subscribe()
            binding.roomChatEditText.text.clear()
        }
        binding.drawClearAll.setOnClickListener {
            binding.draw.clear()
            stompClient.send("/pub/canvas/clear", roomId).subscribe()
            binding.drawColorBlack.visibility = View.GONE
            binding.drawColorRed.visibility = View.GONE
            binding.drawColorGreen.visibility = View.GONE
            binding.drawColorBlue.visibility = View.GONE
        }
        binding.drawPencil.setOnClickListener {
            binding.draw.currentColor = Color.BLACK
            binding.draw.currentWidth = 10F
            binding.draw.paint.strokeCap = Paint.Cap.BUTT
            binding.drawColorBlack.visibility = View.VISIBLE
            binding.drawColorRed.visibility = View.VISIBLE
            binding.drawColorGreen.visibility = View.VISIBLE
            binding.drawColorBlue.visibility = View.VISIBLE
        }
        binding.drawEraser.setOnClickListener {
            binding.draw.currentColor = Color.WHITE
            binding.draw.currentWidth = 50F
            binding.draw.paint.strokeCap = Paint.Cap.ROUND
            binding.drawColorBlack.visibility = View.GONE
            binding.drawColorRed.visibility = View.GONE
            binding.drawColorGreen.visibility = View.GONE
            binding.drawColorBlue.visibility = View.GONE
        }
        binding.drawColorBlack.setOnClickListener {
            binding.draw.currentColor = Color.BLACK
        }
        binding.drawColorRed.setOnClickListener {
            binding.draw.currentColor = Color.RED
        }
        binding.drawColorGreen.setOnClickListener {
            binding.draw.currentColor = Color.GREEN
        }
        binding.drawColorBlue.setOnClickListener {
            binding.draw.currentColor = Color.BLUE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }
}