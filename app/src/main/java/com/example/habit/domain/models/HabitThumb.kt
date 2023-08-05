package com.example.habit.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.HashMap
@Parcelize
data class HabitThumb(
    var id:String,
    var title:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var entries: HashMap<LocalDate,Entry>? = null
) : Parcelable
