package com.example.habit.data.network

import com.example.habit.data.network.model.AddHabitResponseModel.AddHabitResponseModel
import com.example.habit.data.network.model.GroupHabitModel.GroupHabitModel
import com.example.habit.data.network.model.GroupHabitModel.GroupHabitsModel
import com.example.habit.data.network.model.HabitsListModel.HabitModel
import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import com.example.habit.data.network.model.UpdateHabitEntriesModel.EntriesModel
import com.example.habit.data.network.model.UserIdsModel.UserIdsModel
import retrofit2.Call
import retrofit2.Response
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

    @GET("/habit/group/get_habits")
    fun getGroupHabits():Call<GroupHabitsModel>

    @POST("/habit/add_habit")
    suspend fun addHabit( @Body habit:HabitModel):Response<AddHabitResponseModel>

    @POST("/habit/group/add_habit/{adminHabitId}")
    fun addGroupHabit(@Body habit: GroupHabitModel,@Path("adminHabitId") habitId: String):Call<Any>

    @PUT("/habit/update_habit/{id}")
    fun updateHabit( @Body habit:HabitModel, @Path("id") id: String?):Call<Any>

    @PATCH("/habit/update_habit_entries/{id}")
    fun updateHabitEntries( @Body habitEntries: EntriesModel, @Path("id") id:String):Call<Any>

    @PATCH("/habit/group/update_habit/{groupId}")
    fun updateGroupHabit(@Body groupHabitModel:GroupHabitModel,@Path("groupId") id:String):Call<Any>

    @DELETE("/habit/group/delete_habit/{groupId}")
    fun deleteGroupHabit(@Path("groupId") id:String):Call<Any>

    @DELETE("/habit/delete_habit/{id}")
    fun deleteHabit(  @Path("id") id:String):Call<Any>

    @PATCH("habit/group/remove_member/{habitGroupId}")
    fun removeMemberFromGroup(@Path("habitGroupId") habitGroupId:String, @Body userIds: UserIdsModel):Call<Any>

    @PATCH("habit/group/add_member/{habitGroupId}")
    fun addMembersToGroup(@Path("habitGroupId") habitGroupId:String, @Body userIds: UserIdsModel):Call<Any>
}