package com.habitude.habit.data.network.model.HabitRequestModel

import com.habitude.habit.data.network.model.UsersModel.UserModel
import com.google.gson.annotations.SerializedName

data class HabitRequestsModel(
    @SerializedName("data")
    val habitRequests:List<HabitRequestModel>
)
data class HabitRequestModel(
    val from:UserModel,
    val to : String,
    val startDate:String,
    val endDate:String,
    val habitTitle:String,
    val groupHabitId:String
)
