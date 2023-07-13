package com.example.habit.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class Habit(
    var id:Int?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime: LocalDateTime?,
) : Parcelable{
    constructor():this(null,null,null,null,null,null,null,null,)
}