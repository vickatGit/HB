package com.example.habit.ui.model.User

data class UserView(
    val id: String? = null,
    val email: String? = null,
    val username: String? = null,
    var userBio:String? = null,
    val followers:String? = null,
    val followings:String? = null,
)
