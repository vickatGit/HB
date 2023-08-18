package com.example.habit.ui.model

import java.time.LocalDate
import java.time.LocalDateTime

data class GroupHabitView(
    var id:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime: LocalDateTime?,
)
