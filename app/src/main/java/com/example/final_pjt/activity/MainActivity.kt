package com.example.final_pjt.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.R
import com.example.final_pjt.adapter.RoomAdapter
import com.example.final_pjt.databinding.ActivityMainBinding
import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail
import com.example.final_pjt.dto.RoomStatusEnum
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
import com.example.final_pjt.viewmodel.RoomViewModel

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    private lateinit var roomAdapter: RoomAdapter
    val viewModel:RoomViewModel by viewModels()
    var rooms = mutableListOf<RoomDetail>()
    var user: User? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.btnMainCreateRoom.setOnClickListener {
            showDialog()
        }
        viewModel.roomLiveData.observe(this, Observer {
            Log.d(TAG, "onCreate: observe")
            roomAdapter.rooms = it
            roomAdapter.notifyDataSetChanged()
        })
        binding.mainSwipe.setOnRefreshListener {
            getRooms()
        }
        
    }

    private fun init(){
        initLoginUser()
        setAdapter()

    }

    override fun onResume() {
        super.onResume()
        getRooms()
    }



    private fun initLoginUser(){
        var flag = intent.getIntExtra("flag",-1)
        if(flag==2) {
            user = intent.getSerializableExtra("data") as User
            user!!.userToken = FirebaseAuth.getInstance().uid.toString()
            if(user!!.userToken!=null) {
                insertUser(user!!)
            }
        }
    }

    private fun setAdapter(){
        roomAdapter = RoomAdapter(mutableListOf<RoomDetail>())
        roomAdapter.setOnRoomClickListener(object :RoomAdapter.OnRoomClickListener {
            override fun onRoomClickListener(view: View, position: Int) {
                val service = ApplicationClass.retrofit.create(RoomService::class.java)
                service.getRoomDetail(rooms[position].roomId).enqueue(object : Callback<RoomDetail> {
                    override fun onResponse(
                        call: Call<RoomDetail>,
                        response: Response<RoomDetail>
                    ) {
                        var data = response.body()!!
                        if(data.numOfPeople>=data.maxNumOfPeople||data!!.roomStatus== RoomStatusEnum.PROCEEDING){
                            showDialogError2()
                        }else{
                            var intent = Intent(this@MainActivity,RoomActivity::class.java)
                            intent.putExtra("roomId",rooms.get(position).roomId)
                            startActivity(intent)
                        }

                    }
                    override fun onFailure(call: Call<RoomDetail>, t: Throwable) {
                        //통신 실패 = 방이 사라짐
                        showDialogError()
                    }
                })
            }
        })
        binding.recyclerMainRoom.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalSpaceItemDecoration(20))
        }
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
                    if(response.body()!!.userId!=null) {
                        sharedPreferencesUtil.putUser(response.body()!!)
                        Log.d(TAG, "onResponse: ${response.body()!!.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }



    private fun getRooms(){
        val service = ApplicationClass.retrofit.create(RoomService::class.java)
        service.getRooms().enqueue(object : Callback<MutableList<RoomDetail>>{
            override fun onResponse(
                call: Call<MutableList<RoomDetail>>,
                response: Response<MutableList<RoomDetail>>
            ) {
                viewModel.setData(response.body()!!)
                rooms.clear()
                rooms = response.body()!!
                Log.d(TAG, "onResponse: ${response.body()!!}")
                roomAdapter.notifyDataSetChanged()
                binding.mainSwipe.isRefreshing = false
            }
            override fun onFailure(call: Call<MutableList<RoomDetail>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })

    }

    fun showDialogError2(){
        var builder = AlertDialog.Builder(this, androidx.appcompat.R.style.AlertDialog_AppCompat)
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_enter_room_error_no_enter_room,findViewById(R.id.error_no_enter_room))
        builder.setView(view)

        val alertDialog = builder.create()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)
        var pointWidth = (point.x * 0.7).toInt()
        var pointHeight = (point.y * 0.2).toInt()

        view.findViewById<AppCompatButton>(R.id.error_no_enter_room_btn).setOnClickListener {
            alertDialog.dismiss()
            getRooms()
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()

    }
    fun showDialogError(){
        var builder = AlertDialog.Builder(this, androidx.appcompat.R.style.AlertDialog_AppCompat)
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_enter_room_error_no_room,findViewById(R.id.error_no_room))
        builder.setView(view)

        val alertDialog = builder.create()
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)
        var pointWidth = (point.x * 0.7).toInt()
        var pointHeight = (point.y * 0.2).toInt()

        view.findViewById<AppCompatButton>(R.id.error_no_room_btn).setOnClickListener {
            alertDialog.dismiss()
            getRooms()
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()

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

        var people2 = view.findViewById<AppCompatCheckBox>(R.id.alert_checkbox_2)
        var people3 = view.findViewById<AppCompatCheckBox>(R.id.alert_checkbox_3)
        var people4 = view.findViewById<AppCompatCheckBox>(R.id.alert_checkbox_4)
        var now_checked:AppCompatCheckBox? = null
        people2.setOnClickListener {
            if(now_checked==null){
                now_checked = people2
            }else{
                now_checked!!.isChecked = false
                now_checked = people2
            }
        }
        people3.setOnClickListener {
            if(now_checked==null){
                now_checked = people3
            }else{
                now_checked!!.isChecked = false
                now_checked = people3
            }
        }
        people4.setOnClickListener {
            if(now_checked==null){
                now_checked = people4
            }else{
                now_checked!!.isChecked = false
                now_checked = people4
            }
        }

        var time1 = view.findViewById<AppCompatCheckBox>(R.id.alert_time_checkbox_1)
        var time2 = view.findViewById<AppCompatCheckBox>(R.id.alert_time_checkbox_2)
        var nowTime_checked:AppCompatCheckBox? = null

        time1.setOnClickListener {
            if(nowTime_checked==null){
                nowTime_checked = time1
            }else{
                nowTime_checked!!.isChecked = false
                nowTime_checked = time1
            }
        }
        time2.setOnClickListener {
            if(nowTime_checked==null){
                nowTime_checked = time2
            }else{
                nowTime_checked!!.isChecked = false
                nowTime_checked = time2
            }
        }


        view.findViewById<AppCompatButton>(R.id.alert_cancle_btn).setOnClickListener {
            alertDialog.dismiss()
        }

        view.findViewById<AppCompatButton>(R.id.alert_ok_btn).setOnClickListener {
            var room = Room(sharedPreferencesUtil.getUser(),roomName.text.toString(),nowTime_checked?.text!!.split("초")[0].toInt(),now_checked?.text!!.split("명")[0].toInt())
            Log.d(TAG, "showDialog: ${room.toString()}")
            /**
             * 방 성시 서버에 생성 응답 요청
             */
            if(now_checked!=null&&nowTime_checked!=null) {
                val service = ApplicationClass.retrofit.create(RoomService::class.java)
                service.createRoom(room).enqueue(object : Callback<RoomDetail> {
                    override fun onResponse(
                        call: Call<RoomDetail>,
                        response: Response<RoomDetail>
                    ) {
                        if (response.isSuccessful) {
                            var intent = Intent(this@MainActivity, RoomActivity::class.java)
                            intent.putExtra("roomId", response.body()!!.roomId)
                            startActivity(intent)
                            viewModel.addRoom(response.body()!!)
                            alertDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<RoomDetail>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.width = pointWidth
        alertDialog.window!!.attributes.height = pointHeight
        alertDialog.show()
    }


}