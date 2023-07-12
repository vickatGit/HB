package com.example.habit.ui.model

import java.time.LocalDate

data class HabitThumbView (
    var id:Int,
    var title:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
)