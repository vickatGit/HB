package com.example.habit.domain.models.notification

import com.example.habit.domain.models.User.User
import java.time.LocalDate

data class HabitRequest(
    val from: User,
    val to: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val habitTitle:String,
    val groupHabitId:String
)