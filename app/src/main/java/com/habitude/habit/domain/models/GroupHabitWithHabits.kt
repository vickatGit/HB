package com.habitude.habit.domain.models

data class GroupHabitWithHabits(
    val habitGroup:GroupHabit,
    val habits : List<Habit>
)
