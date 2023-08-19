package com.example.habit.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class HabitView(
    var id:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime: LocalDateTime?,
    var entries:HashMap<LocalDate,EntryView>? = null,
    var habitGroupId:String?=null,
    var userId:String?=null
) : Parcelable {
    constructor():this("",null,null,null,null,null,null,null,null)
}