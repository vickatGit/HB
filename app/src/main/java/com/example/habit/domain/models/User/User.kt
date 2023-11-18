package com.example.habit.domain.models.User

data class User(
    val id: String? = null,
    val email: String? = null,
    val username: String? = null,
    val userBio:String? = null,
    val followers:String? = null,
    val followings:String? = null,
    val avatarUrl:String? = null
)
