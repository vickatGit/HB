package com.example.habit.ui.model

import android.os.Parcelable
import com.example.habit.domain.models.Member
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
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
    var members : List<MemberView>? = null
) : Parcelable{
    constructor():this("",null,null,null,null,null,null,null,null)
}
