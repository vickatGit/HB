package com.example.habit.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.Duration
import java.time.LocalDateTime

@Parcelize
@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    var name:String,
    var description:String,
    var dayFrequency:Int,
    var startDate:LocalDateTime,
    var endDateTime: LocalDateTime,
    var isReminderOn : Boolean,
    var duration: Duration,
    var reminderTime:LocalDateTime,
) : Parcelable
