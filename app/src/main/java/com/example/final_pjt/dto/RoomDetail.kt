package com.example.final_pjt.dto

data class RoomDetail(

    var userSet: List<User>,
    var roomId:String,
    var roomMaster : String,
    var roomName  :String,
    var nowDrawer : String,
    var numOfPeople : Int,
    var status : GameStatusEnum,
    var maxNumOfPeople : Int,
    var gameTime : Int,
    var roomStatus : RoomStatusEnum,
    var answer : String
):java.io.Serializable{
    override fun toString(): String {
        return "RoomDetail(userSet=$userSet, roomId='$roomId', roomMaster='$roomMaster', roomName='$roomName', nowDrawer='$nowDrawer', numOfPeople=$numOfPeople, status='$status', maxNumOfPerople=$maxNumOfPeople)"
    }
}
