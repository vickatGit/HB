package com.example.habit.data.network.model.UsersModel

import com.google.gson.annotations.SerializedName

data class UsersModel(
	val data: List<UserModel> = emptyList()
)

data class UserModel(
	@SerializedName("_id")
	val id: String? = null,
	val email: String? = null,
	val username: String? = null,
	val userBio:String? = null,
	val followers:String? = null,
	val followings:String? = null,
	val v: Int? = null,

)

data class ProfileModel(
	val data: UserModel? = null
)

