package com.example.habit.data.network.model.FollowModel

import com.example.habit.data.network.model.UsersModel.UserModel
import com.example.habit.domain.models.User.User
import com.google.gson.annotations.SerializedName

data class FollowingModel(
	@SerializedName("data")
	val users: List<UserTo?>? = null
)

data class UserTo(
	val v: Int? = null,
	val to: UserModel? = null
)



