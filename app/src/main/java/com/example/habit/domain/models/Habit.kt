package com.example.habit.domain.models

import android.os.Parcelable
import android.util.ArrayMap
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.HashMap

@Parcelize
data class Habit(
    var id:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime: LocalDateTime?,
    var entries:HashMap<LocalDate,Entry>? = null,
    var habitGroupId:String?=null,
    var userId:String?=null,
    var habitGroupLocalId:String?,
) : Parcelable{
    constructor():this("",null,null,null,null,null,null,null,null, habitGroupLocalId = null)
}
