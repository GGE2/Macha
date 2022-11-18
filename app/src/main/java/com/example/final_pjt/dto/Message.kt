package com.example.final_pjt.dto

import com.google.gson.annotations.SerializedName

class Message (
    @SerializedName(value = "user")
    var user : User,
    @SerializedName(value = "message")
    var text : String,
        ){
    override fun toString(): String {
        return "Message(user=$user, text='$text')"
    }
}