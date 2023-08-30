package com.example.habit.data.network.model.GroupHabitModel

import com.example.habit.data.network.model.HabitsListModel.EntryModel
import com.example.habit.data.network.model.HabitsListModel.HabitModel
import com.google.gson.annotations.SerializedName

data class GroupHabitsModel(
	val data: List<GroupHabitModel?>? = null
)

data class GroupHabitDataModel(
	val data: GroupHabitModel? = null
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
	val habits: List<HabitModel?>? = null,
	val admin: String? = null,
	val members: List<MemberModel?>? = null,
	val v: Int? = null

)
data class MemberModel(
	@SerializedName("_id")
	val userId: String? = null,
	val username: String? = null,

)

