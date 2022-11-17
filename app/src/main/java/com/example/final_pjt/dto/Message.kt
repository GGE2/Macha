package com.example.final_pjt.dto

class Message (
    var userToken : String,
    var text : String,
        ){
    override fun toString(): String {
        return "Message(userToken='$userToken', text='$text')"
    }
}