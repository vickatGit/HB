package com.habitude.habit.data.network.model.FollowModel

import com.habitude.habit.data.network.model.UsersModel.UserModel
import com.google.gson.annotations.SerializedName


data class FollowerModel(
    @SerializedName("data")
    val users: List<UserModel?>? = null
)

data class UserFollowsModel(
    val v: Int? = null,
    val follows: UserModel? = null
)


