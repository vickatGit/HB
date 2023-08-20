package com.example.habit.ui.model

data class GroupHabitWithHabitsView(
    val habitGroup:GroupHabitView,
    val habits : List<HabitView>
)
