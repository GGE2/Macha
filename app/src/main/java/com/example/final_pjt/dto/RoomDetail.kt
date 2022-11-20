package com.example.final_pjt.dto

data class RoomDetail(

    var userSet: List<User>,
    var roomId:String,
    var roomMaster : String,
    var roomName  :String,
    var nowDrawer : String,
    var numOfPeople : Int,
    var gameStatus : String,
    var maxNumOfPeople : Int,
):java.io.Serializable{
    override fun toString(): String {
        return "RoomDetail(userSet=$userSet, roomId='$roomId', roomMaster='$roomMaster', roomName='$roomName', nowDrawer='$nowDrawer', numOfPeople=$numOfPeople, gameStatus='$gameStatus', maxNumOfPerople=$maxNumOfPeople)"
    }
}
