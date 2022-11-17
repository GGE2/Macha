package com.example.final_pjt.dto

data class User(
    var userToken:String,
    var nickname : String,
    var profileImg : String,
    var isOnline : Int = 1


) {
    override fun toString(): String {
        return "User(userToken='$userToken', nickname='$nickname', profileImg='$profileImg', isOnline=$isOnline)"
    }
}
