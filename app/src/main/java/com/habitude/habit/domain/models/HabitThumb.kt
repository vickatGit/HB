package com.habitude.habit.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.HashMap
@Parcelize
data class HabitThumb(
    var id:String,
    var title:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var entries: HashMap<LocalDate,Entry>? = null,
    var reminderTime: LocalDateTime?,
    var reminderDate: String?
) : Parcelable
