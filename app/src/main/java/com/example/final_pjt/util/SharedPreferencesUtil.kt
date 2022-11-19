package com.example.final_pjt.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.final_pjt.dto.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesUtil(context : Context) {
    val SHARED_PREFERENCES_NAME = "final_pjt_preference"
    val SHARED_PREP_MESSAGE_NAME = "final_pjt_message"
    val preferences:SharedPreferences = context.getSharedPreferences(SHARED_PREP_MESSAGE_NAME,Context.MODE_PRIVATE)

    //사용자 정보 저장
    @SuppressLint("CommitPrefEdits")
    fun putUser(user: User){
        val gson = Gson()
        val json = gson.toJson(user)
        val editor = preferences.edit()
        editor.putString("user",json)
        editor.apply()
    }
    fun getUser():User{
        val json = preferences.getString("user",null)
        val gson = Gson()
        val data:User= gson.fromJson(json,object: TypeToken<User>(){}.type)
        return data
    }

}