package com.example.final_pjt.service

import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail
import retrofit2.Call
import retrofit2.http.*

interface RoomService {

    @POST("game/room")
    fun createRoom(@Body room: Room) : Call<RoomDetail>
    @GET("game/rooms")
    fun getRooms() : Call<MutableList<RoomDetail>>
    @GET("game/room")
    fun getRoomDetail(@Query("roomId") roomId: String) : Call<RoomDetail>

}