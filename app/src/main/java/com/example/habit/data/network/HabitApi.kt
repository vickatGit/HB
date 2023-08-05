package com.example.habit.data.network

import retrofit2.Call
import retrofit2.http.GET

interface HabitApi {
    @GET("/habit/get_habits")
    fun getHabits():Call<Any>
}