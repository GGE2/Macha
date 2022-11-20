package com.example.final_pjt.dto

class Room (
    var user : User,
    var roomName:String,
    var gameTime : Int,
    var maxNumOfPeople:Int,
        ){
    override fun toString(): String {
        return "Room(user=$user, roomName='$roomName', gameTime=$gameTime, maxNumOfPeople=$maxNumOfPeople)"
    }
}