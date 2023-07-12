package com.example.habit.domain.models

import java.time.LocalDate

data class HabitThumb(
    var id:Int,
    var title:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
)
