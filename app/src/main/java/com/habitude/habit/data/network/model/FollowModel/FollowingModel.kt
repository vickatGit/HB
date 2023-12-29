package com.habitude.habit.data.network.model.FollowModel

import com.habitude.habit.data.network.model.UsersModel.UserModel
import com.habitude.habit.domain.models.User.User
import com.google.gson.annotations.SerializedName

data class FollowingModel(
	@SerializedName("data")
	val users: List<UserModel?>? = null
)

data class UserTo(
	val v: Int? = null,
	val to: UserModel? = null
)



