package com.example.habit.data.network.model.HabitsListModel

import com.google.gson.annotations.SerializedName

data class HabitsListModel(
	val data: List<HabitModel>
)

data class HabitModel(
	val entries: List<EntryModel>? = null,
	val reminderQuestion: String? = null,
	val endDate: String? = null,
	val v: Int? = null,
	val isReminderOn: Boolean? = null,
	val description: String? = null,
	@SerializedName("_id")
	val id: String? = null,
	val reminderTime: String? = null,
	val title: String? = null,
	val startDate: String? = null
)

data class EntryModel(
	val score: Int? = null,
	val completed: Boolean? = null,
	val id: String? = null,
	val timestamp: String? = null
)

