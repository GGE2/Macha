package com.example.final_pjt.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.R
import com.example.final_pjt.adapter.RoomAdapter
import com.example.final_pjt.databinding.ActivityMainBinding
import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail
import com.example.final_pjt.dto.User
import com.example.final_pjt.service.RoomService
import com.example.final_pjt.service.UserService
import com.example.final_pjt.util.ApplicationClass
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import com.example.final_pjt.util.ApplicationClass.Companion.sharedPreferencesUtil
private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    private lateinit var roomAdapter: RoomAdapter
    var rooms = listOf<RoomDetail>()
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.btnMainCreateRoom.setOnClickListener {
            showDialog()
        }
        binding.btnMainRefresh.setOnClickListener {
            getRooms()
        }
    }

    private fun init(){
        initLoginUser()
        temp()
        setAdapter()

    }
    private fun initLoginUser(){
        var flag = intent.getIntExtra("flag",-1)
        if(flag==2) {
            user = intent.getSerializableExtra("data") as User
            user!!.userToken = FirebaseAuth.getInstance().uid.toString()
            if(user!!.userToken!=null) {
                insertUser(user!!)
                Log.d(TAG, "initLoginUser: ${user.toString()}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getRooms()
    }

    private fun setAdapter(){
        roomAdapter = RoomAdapter(listOf<RoomDetail>())
        roomAdapter.setOnRoomClickListener(object :RoomAdapter.OnRoomClickListener {
            override fun onRoomClickListener(view: View, position: Int) {
                var intent = Intent(this@MainActivity,RoomActivity::class.java)
                intent.putExtra("roomId",rooms.get(position).roomId)
                Log.d(TAG, "onRoomClickListener: ${rooms.get(position).roomId}")
                startActivity(intent)
            }
        })
        binding.recyclerMainRoom.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalSpaceItemDecoration(20))
        }
    }

    private fun temp(){

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
    /**
     *  구글 로그인 성공시 server에 User 객체 전송
     */
    private fun insertUser(user : User){
        val service = ApplicationClass.retrofit.create(UserService::class.java)
        service.insertUser(user).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.d(TAG, "onResponse: 유저 정보 통신완료${response.body()!!.toString()}")
                    sharedPreferencesUtil.putUser(response.body()!!)
                    Log.d(TAG, "onResponse: test")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
    private fun getRooms(){
        val service = ApplicationClass.retrofit.create(RoomService::class.java)
        service.getRooms().enqueue(object : Callback<List<RoomDetail>>{
            override fun onResponse(
                call: Call<List<RoomDetail>>,
                response: Response<List<RoomDetail>>
            ) {
                roomAdapter.rooms = response.body()!!
                rooms = response.body()!!
                roomAdapter.notifyDataSetChanged()
                Log.d(TAG, "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<List<RoomDetail>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
    fun showDialog(){
        var builder = AlertDialog.Builder(this, androidx.appcompat.R.style.AlertDialog_AppCompat)
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_create_room,findViewById(R.id.create_dialog))
        builder.setView(view)
        val alertDialog = builder.create()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)

        var pointWidth = (point.x * 0.8).toInt()
        var pointHeight = (point.y * 0.4).toInt()

        var roomName = view.findViewById<EditText>(R.id.alert_edit_title)

        view.findViewById<AppCompatButton>(R.id.alert_cancle_btn).setOnClickListener {
            alertDialog.dismiss()
        }
        view.findViewById<AppCompatButton>(R.id.alert_ok_btn).setOnClickListener {
            var room = Room(sharedPreferencesUtil.getUser(),roomName.text.toString(),4,2)
            /**
             * 방 성시 서버에 생성 응답 요청
             */
            val service = ApplicationClass.retrofit.create(RoomService::class.java)
            service.createRoom(room).enqueue(object : Callback<RoomDetail>{
                override fun onResponse(call: Call<RoomDetail>, response: Response<RoomDetail>) {
                    if(response.isSuccessful){
                        var intent = Intent(this@MainActivity,RoomActivity::class.java)
                        intent.putExtra("roomId",response.body()!!.roomId)
                        Log.d(TAG, "onResponse: ${response.body()!!.roomId}")
                        startActivity(intent)
                        alertDialog.dismiss()
                    }
                }
                override fun onFailure(call: Call<RoomDetail>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
        }

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()
    }


}