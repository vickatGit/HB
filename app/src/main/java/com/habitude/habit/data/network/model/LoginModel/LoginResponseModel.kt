package com.habitude.habit.data.network.model.LoginModel

data class LoginResponseModel(val message: String, val token:String, val userId:String, val userName:String, val success : Boolean, val error:String)