package com.habitude.habit.ui.model

import java.time.LocalDate

data class HabitThumbView (
    var id:String,
    var title:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var entries:HashMap<LocalDate,EntryView>? = null
)