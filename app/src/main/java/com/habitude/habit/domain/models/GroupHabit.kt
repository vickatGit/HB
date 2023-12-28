package com.habitude.habit.domain.models

import java.time.LocalDate
import java.time.LocalDateTime

data class GroupHabit(
    var id:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime: LocalDateTime?,
    var members : List<Member>? = null,
    var admin: String? = null
)
