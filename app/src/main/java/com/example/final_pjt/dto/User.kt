package com.example.final_pjt.dto

data class User(
    var userToken:String,
    var profileImg : String,
    var nickname : String,
    var isOnline : Int=1
    ) : java.io.Serializable{
    var userId: Int = -2
    constructor(userId:Int,userToken:String,profileImg : String,nickname : String,isOnline : Int) : this(profileImg,profileImg,nickname,isOnline)
    override fun toString(): String {
        return "User(userToken='$userToken', profileImg='$profileImg', nickname='$nickname', isOnline=$isOnline, userId=$userId)"
    }

}
