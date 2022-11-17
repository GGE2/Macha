package com.example.final_pjt.dto

class Room (
    var roomId:String,
    var roomName:String,
        ){
    override fun toString(): String {
        return "Room(roomId='$roomId', roomName='$roomName')"
    }
}