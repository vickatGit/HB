package com.example.habit.data.Mapper

/**
 * A = HabitEntity
 * V = HabitThumb
 * D = Habit
 */

interface Mapper<A,V,D> {
    fun mapFromHabitEntity(type : A) : V
    fun mapToHabitEntity(type : D ) : A

    fun mapToHabit(type: A):D

}