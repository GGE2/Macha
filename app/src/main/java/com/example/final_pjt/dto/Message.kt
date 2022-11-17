package com.example.final_pjt.dto

import com.google.gson.annotations.SerializedName

class Message (
    @SerializedName(value = "writer")
    var userToken : String,
    @SerializedName(value = "message")
    var text : String,
        ){
    override fun toString(): String {
        return "Message(userToken='$userToken', text='$text')"
    }
}