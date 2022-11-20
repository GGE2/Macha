package com.example.final_pjt.service

import com.example.final_pjt.dto.Room
import com.example.final_pjt.dto.RoomDetail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomService {

    @POST("game/room")
    fun createRoom(@Body room: Room) : Call<RoomDetail>
    @GET("game/rooms")
    fun getRooms() : Call<MutableList<RoomDetail>>
    @GET("game/{roomId}")
    fun getRoomDetail(@Path("roomId") roomId: String) : Call<RoomDetail>

}