package com.example.habit.data.network

import androidx.room.Delete
import com.example.habit.data.network.model.HabitsListModel.HabitModel
import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import com.example.habit.data.network.model.UpdateHabitEntriesModel.EntriesModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HabitApi {
    @GET("/habit/get_habits")
    fun getHabits():Call<HabitsListModel>

    @POST("/habit/add_habit")
    fun addHabit(@Body habit:HabitModel):Call<Any>

    @PUT("/habit/update_habit/{id}")
    fun updateHabit(@Body habit:HabitModel,@Path("id") id:String):Call<Any>

    @PATCH("/habit/update_habit_entries/{id}")
    fun updateHabitEntries(@Body habitEntries: EntriesModel, @Path("id") id:String):Call<Any>

    @DELETE("/habit/delete_habit/{id}")
    fun deleteHabit( @Path("id") id:String):Call<Any>
}