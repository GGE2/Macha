package com.example.final_pjt.service

import com.example.final_pjt.dto.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("rest/user/login")
    fun insertUser(@Body user:User): Call<Unit>

}