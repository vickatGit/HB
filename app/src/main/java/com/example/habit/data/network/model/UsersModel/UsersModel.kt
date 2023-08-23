package com.example.habit.data.network.model.UsersModel

data class UsersModel(
	val data: List<User> = emptyList()
)

data class User(
	val v: Int? = null,
	val id: String? = null,
	val email: String? = null,
	val username: String? = null
)

