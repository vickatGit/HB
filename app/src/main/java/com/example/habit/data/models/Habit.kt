package com.example.habit.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate:LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime:LocalDateTime?,
) : Parcelable{
    constructor():this(null,null,null,null,null,null,null,null,)
}
