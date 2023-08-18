package com.example.habit.data.network.model.GroupHabitModel

import com.google.gson.annotations.SerializedName

data class GroupHabitsModel(
	val data: List<GroupHabitModel?>? = null
)

data class GroupHabitModel(
	@SerializedName("_id")
	val id: String? = null,
	val localId: String,
	val description: String?,
	val title: String,
	val startDate: String?,
	val endDate: String? ,
	val isReminderOn: Boolean? ,
	val reminderQuestion: String? ,
	val reminderTime: String? ,
	val habits: List<String?>? = null,
	val admin: String? = null,
	val members: List<String?>? = null,
	val v: Int? = null

)

