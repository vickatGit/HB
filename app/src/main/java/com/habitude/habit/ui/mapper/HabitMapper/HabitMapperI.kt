package com.habitude.habit.ui.mapper.HabitMapper

/**
 * A - Habit (ui)
 * V - Habit (domain)
 * D - HabitThumb (domain)
 * C - HabitThumbView (ui)
 */

interface HabitMapperI<A,V,D,C> {
    fun mapFromHabit(type : A) : V

    fun mapToHabit(type: V) :A

    fun mapToHabitThumbView(type: D) : C
}