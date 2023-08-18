package com.example.habit.ui.mapper.GroupHabitMapper

/**
 * A : GroupHabitView
 * V : GroupHabit
 * D : HabitView
 */
interface GroupHabitMapperI<A,V,D> {
    fun toGroupHabit(type : A):V
    fun toGroupHabitView(type : V):A
    fun toGroupHabitFromHabit(type: D):V
}