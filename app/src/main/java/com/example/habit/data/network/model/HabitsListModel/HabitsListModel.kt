package com.example.habit.data.network.model.HabitsListModel

import com.google.gson.annotations.SerializedName

data class HabitsListModel(
	val data: List<HabitModel>
)

data class HabitModel(
	val entries: List<EntryModel>? = null,
	val reminderQuestion: String? = null,
	var endDate: String? = null,
	val v: Int? = null,
	val isReminderOn: Boolean? = null,
	var description: String? = null,
	@SerializedName("_id")
	val serverId: String? = null,
	val localId:String? = null,
	val reminderTime: String? = null,
	var title: String? = null,
	var startDate: String? = null,
	@SerializedName("groupHabitId")
	var habitGroupId:String?=null,
	var userId:String?=null
)

data class EntryModel(
	val score: Int? = null,
	val completed: Boolean? = null,
	val id: String? = null,
	val timestamp: String? = null
)

