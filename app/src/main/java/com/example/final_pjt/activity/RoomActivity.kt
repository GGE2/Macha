package com.example.final_pjt.activity

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.final_pjt.R
import com.example.final_pjt.adapter.ChatAdapter
import com.example.final_pjt.adapter.UserAdapter
import com.example.final_pjt.databinding.ActivityRoomBinding
import com.example.final_pjt.databinding.DialogGameEndBinding
import com.example.final_pjt.dto.*
import com.example.final_pjt.util.ApplicationClass.Companion.sharedPreferencesUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.Collections.emptyList


private const val TAG = "RoomActivity_싸피"

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    private var list: MutableList<Message> = mutableListOf()
    private val url = "ws://54.180.24.155:8080/stomp/game/websocket"
    private lateinit var userAdapter: UserAdapter
    private lateinit var chatAdapter: ChatAdapter
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
    private val userDataWithRoomId = JSONObject()
    private var roomDetail: RoomDetail? = null
    private val auth = FirebaseAuth.getInstance()
    private val user = sharedPreferencesUtil.getUser()
    private var connected = false
    var roomId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomId = intent.getStringExtra("roomId")
        Log.d(TAG, "onCreate: ${roomId}")
        userAdapter = UserAdapter(emptyList())
        chatAdapter = ChatAdapter(emptyList())
        binding.roomUserRecyclerView.apply{
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@RoomActivity,LinearLayoutManager.HORIZONTAL,false)
            addItemDecoration(HorizonSpaceItemDecoration(20))
        }
        binding.draw.roomId = roomId!!
        binding.roomStartButton.setOnClickListener {
            binding.roomStartButton.visibility = View.GONE
            stompClient.send("/pub/game/round-start", roomId).subscribe()
        }
        binding.roomChatRecyclerView.adapter = ChatAdapter(listOf())
        binding.roomChatRecyclerView.layoutManager = LinearLayoutManager(this)
        connectStomp()
        stompClient.send("/pub/room/enter", userDataWithRoomId.toString()).subscribe()
        binding.roomChatSendButton.setOnClickListener {
            if(!binding.roomChatEditText.text.isNullOrEmpty()){
                val data = JSONObject()
                data.put("message", binding.roomChatEditText.text.toString())
                val userJson = JSONObject()
                userJson.put("userToken", auth.currentUser?.uid)
                userJson.put("nickname", auth?.currentUser?.displayName!!)
                userJson.put("profileImg", auth.currentUser?.photoUrl!!.toString())
                userJson.put("userId", user.userId)
                userJson.put("isOnline", 1)
                data.put("user", userJson)
                data.put("roomId", "${roomId}")
                data.put("messageType", MessageTypeEnum.USER_CHAT)
                stompClient.send("/pub/chat/message", data.toString()).subscribe()
                binding.roomChatEditText.setText("")
            }
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
            binding.draw.currentColor = Color.rgb(143,128,116)
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
        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.i("OPEND", "!!")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.i("CLOSED", "!!")
                    connected = false
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.i("ERROR", "!!")
                    Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
                    connected = false
                }
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }
    }

    override fun onBackPressed() {
        showDialogExit()
    }
    fun showDialogExit(){
        var builder = android.app.AlertDialog.Builder(this, androidx.appcompat.R.style.AlertDialog_AppCompat)
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_game_exit,findViewById(R.id.dialog_exit))
        builder.setView(view)
        val alertDialog = builder.create()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)
        var pointWidth = (point.x * 0.7).toInt()
        var pointHeight = (point.y * 0.2).toInt()

        view.findViewById<AppCompatButton>(R.id.alert_exit_cancle_btn).setOnClickListener {
            alertDialog.dismiss()
        }
        view.findViewById<AppCompatButton>(R.id.alert_exit_ok_btn).setOnClickListener {
            finish()
            alertDialog.dismiss()
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()

    }

    override fun onResume() {
        super.onResume()
        if(!connected){
            connectStomp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        if(connected){
            connected = false
            stompClient.send("/pub/room/exit", userDataWithRoomId.toString()).subscribe()
            stompClient.disconnect()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        if(connected){
            connected = false
            stompClient.send("/pub/room/exit", userDataWithRoomId.toString()).subscribe()
            stompClient.disconnect()
            finish()
        }
    }

    fun showDialogError(){
        var builder = android.app.AlertDialog.Builder(this, androidx.appcompat.R.style.AlertDialog_AppCompat)
        var view = LayoutInflater.from(this).inflate(
            R.layout.dialog_enter_room_error_no_room,findViewById(
                R.id.error_no_room))
        builder.setView(view)
        view.findViewById<TextView>(R.id.error_no_room_tv).text = "방장이 방을 나갔습니다."
        val alertDialog = builder.create()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)
        var pointWidth = (point.x * 0.7).toInt()
        var pointHeight = (point.y * 0.2).toInt()
        view.findViewById<AppCompatButton>(R.id.error_no_room_btn).setOnClickListener {
            finish()
        }


        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()

    }


    inner class HorizonSpaceItemDecoration(private val horizonSpaceItemDecoration:Int):
            RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = horizonSpaceItemDecoration
        }
            }
    fun showResultDialog(list: List<GameResultDTO>){
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogGameEndBinding.inflate(builder.create().layoutInflater)
        builder.setView(dialogBinding.root)
        if(list.size == 1){
            Glide.with(dialogBinding.root).load(list[0].user.profileImg).into(dialogBinding.user1Img)
            dialogBinding.user1Name.text = list[0].user.nickname
            dialogBinding.user1Score.text = "${list[0].score}점"
            dialogBinding.user2.visibility = View.GONE
            dialogBinding.user2Img.visibility = View.GONE
            dialogBinding.user2Name.visibility = View.GONE
            dialogBinding.user2Score.visibility = View.GONE
            dialogBinding.user3.visibility = View.GONE
            dialogBinding.user3Img.visibility = View.GONE
            dialogBinding.user3Name.visibility = View.GONE
            dialogBinding.user3Score.visibility = View.GONE
            dialogBinding.user4.visibility = View.GONE
            dialogBinding.user4Img.visibility = View.GONE
            dialogBinding.user4Name.visibility = View.GONE
            dialogBinding.user4Score.visibility = View.GONE
        } else if(list.size == 2){
            Glide.with(dialogBinding.root).load(list[0].user.profileImg).into(dialogBinding.user1Img)
            dialogBinding.user1Name.text = list[0].user.nickname
            dialogBinding.user1Score.text = "${list[0].score}점"
            Glide.with(dialogBinding.root).load(list[1].user.profileImg).into(dialogBinding.user2Img)
            dialogBinding.user2Name.text = list[1].user.nickname
            dialogBinding.user2Score.text = "${list[1].score}점"
            dialogBinding.user3.visibility = View.GONE
            dialogBinding.user3Img.visibility = View.GONE
            dialogBinding.user3Name.visibility = View.GONE
            dialogBinding.user3Score.visibility = View.GONE
            dialogBinding.user4.visibility = View.GONE
            dialogBinding.user4Img.visibility = View.GONE
            dialogBinding.user4Name.visibility = View.GONE
            dialogBinding.user4Score.visibility = View.GONE
        } else if(list.size == 3){
            Glide.with(dialogBinding.root).load(list[0].user.profileImg).into(dialogBinding.user1Img)
            dialogBinding.user1Name.text = list[0].user.nickname
            dialogBinding.user1Score.text = "${list[0].score}점"
            Glide.with(dialogBinding.root).load(list[1].user.profileImg).into(dialogBinding.user2Img)
            dialogBinding.user2Name.text = list[1].user.nickname
            dialogBinding.user2Score.text = "${list[1].score}점"
            Glide.with(dialogBinding.root).load(list[2].user.profileImg).into(dialogBinding.user3Img)
            dialogBinding.user3Name.text = list[2].user.nickname
            dialogBinding.user3Score.text = "${list[2].score}점"
            dialogBinding.user4.visibility = View.GONE
            dialogBinding.user4Img.visibility = View.GONE
            dialogBinding.user4Name.visibility = View.GONE
            dialogBinding.user4Score.visibility = View.GONE
        } else{
            Glide.with(dialogBinding.root).load(list[0].user.profileImg).into(dialogBinding.user1Img)
            dialogBinding.user1Name.text = list[0].user.nickname
            dialogBinding.user1Score.text = "${list[0].score}점"
            Glide.with(dialogBinding.root).load(list[1].user.profileImg).into(dialogBinding.user2Img)
            dialogBinding.user2Name.text = list[1].user.nickname
            dialogBinding.user2Score.text = "${list[1].score}점"
            Glide.with(dialogBinding.root).load(list[2].user.profileImg).into(dialogBinding.user3Img)
            dialogBinding.user3Name.text = list[2].user.nickname
            dialogBinding.user3Score.text = "${list[2].score}점"
            Glide.with(dialogBinding.root).load(list[3].user.profileImg).into(dialogBinding.user4Img)
            dialogBinding.user4Name.text = list[3].user.nickname
            dialogBinding.user4Score.text = "${list[3].score}점"
        }
        val alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        dialogBinding.alertResultOkBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun connectStomp(){
        connected = true
        val userJson = JSONObject()
        userJson.put("userToken", auth.currentUser?.uid)
        userJson.put("nickname", auth.currentUser?.displayName!!)
        userJson.put("profileImg", auth.currentUser?.photoUrl!!.toString())
        userJson.put("userId", user.userId)
        userJson.put("isOnline", 1)
        userDataWithRoomId.put("user", userJson)
        userDataWithRoomId.put("roomId", roomId);

        stompClient.connect()
        stompClient.topic("/sub/chat-room/${roomId}").subscribe{
                topicMessage ->
            Log.d(TAG, "onCreate: ${topicMessage.payload}")
            val message = Gson().fromJson(topicMessage.payload, Message::class.java)
            list.add(message)

            runOnUiThread {
                binding.roomChatRecyclerView.adapter = ChatAdapter(list.toList())
                binding.roomChatRecyclerView.scrollToPosition(list.size-1)
            }
        }

        stompClient.topic("/sub/game/status/$roomId").subscribe{
                topicMessage ->
            val message = Gson().fromJson(topicMessage.payload, RoomStatusEnum::class.java)
            Log.d(TAG, "onCreate: $message")
            if(message == RoomStatusEnum.EXIT){
                runOnUiThread {
                    showDialogError()
                }
            }
            if(message == RoomStatusEnum.ANSWER){
                if(roomDetail!!.roomMaster == user.userToken){
                    stompClient.send("/pub/game/round-end", roomId).subscribe()
                }
            }
        }

        stompClient.topic("/sub/game-room/timer/$roomId").subscribe{
                topicMessage ->
            runOnUiThread{
                binding.roomTimerText.text = topicMessage.payload
                if(topicMessage.payload == "0" && roomDetail!!.roomMaster == user.userToken){
                    stompClient.send("/pub/game/round-end", roomId).subscribe()
                }
            }
        }

        stompClient.topic("/sub/game-room/${roomId}").subscribe{
                topicMessage ->
            roomDetail = Gson().fromJson(topicMessage.payload, RoomDetail::class.java)
            runOnUiThread {
                if(roomDetail!!.status == GameStatusEnum.START_ROUND){
                    binding.roomTimerText.visibility = View.VISIBLE
                    binding.roomTimerImage.visibility = View.VISIBLE
                    if(roomDetail!!.nowDrawer == user.userToken){
                        binding.roomAnswerText.text = roomDetail!!.answer
                        binding.roomAnswerText.visibility = View.VISIBLE
                    } else {
                        binding.roomAnswerText.visibility = View.GONE
                    }
                }
                if(roomDetail!!.status == GameStatusEnum.END_GAME){
                    if(roomDetail!!.roomMaster == user.userToken){
                        binding.roomStartButton.visibility = View.VISIBLE
                    }
                    binding.roomTimerImage.visibility = View.GONE
                    binding.roomTimerText.visibility = View.GONE
                    binding.roomAnswerText.visibility = View.GONE
                }
                if(roomDetail!!.roomMaster != user.userToken){
                    binding.roomStartButton.visibility = View.GONE
                }
                userAdapter.users = roomDetail!!.userSet
                userAdapter.notifyDataSetChanged()
                binding.draw.nowDrawer = roomDetail!!.nowDrawer == auth.currentUser?.uid
                binding.draw.stompClient = stompClient
                if(roomDetail!!.nowDrawer == auth.currentUser?.uid){
                    binding.drawClearAll.visibility = View.VISIBLE
                    binding.drawPencil.visibility = View.VISIBLE
                    binding.drawEraser.visibility = View.VISIBLE
                } else {
                    binding.drawClearAll.visibility = View.GONE
                    binding.drawPencil.visibility = View.GONE
                    binding.drawEraser.visibility = View.GONE
                    binding.drawColorBlack.visibility = View.GONE
                    binding.drawColorRed.visibility = View.GONE
                    binding.drawColorGreen.visibility = View.GONE
                    binding.drawColorBlue.visibility = View.GONE
                }
            }
            Log.d(TAG, "onCreate: ${topicMessage.payload}")
            Log.d(TAG, "onCreate: $roomDetail")
        }

        stompClient.topic("/sub/game-room/end/$roomId").subscribe{
                topicMessage ->
            val type = object : TypeToken<List<GameResultDTO>>() {}
            val result = Gson().fromJson<List<GameResultDTO>>(topicMessage.payload, type.type)
            Log.d(TAG, "onCreate: $result")
            runOnUiThread {
                showResultDialog(result)
            }
        }

        stompClient.topic("/sub/canvas-room/${roomId}").subscribe{
                topicMessage ->
            runOnUiThread {
                if(!binding.draw.nowDrawer){
                    binding.draw.addPoint(Gson().fromJson(topicMessage.payload, PointWithRoomId::class.java))
                }
            }
        }

        stompClient.topic("/sub/canvas/clear/${roomId}").subscribe{
                topicMessage ->
            runOnUiThread {
                binding.draw.clear()
            }
        }
    }

}