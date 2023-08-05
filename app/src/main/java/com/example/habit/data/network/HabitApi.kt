package com.example.habit.data.network

import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import retrofit2.Call
import retrofit2.http.GET

interface HabitApi {
    @GET("/habit/get_habits")
    fun getHabits():Call<HabitsListModel>
}