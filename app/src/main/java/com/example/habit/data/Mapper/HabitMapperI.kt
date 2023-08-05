package com.example.habit.data.Mapper

/**
 * A = HabitEntity
 * V = HabitThumb
 * D = Habit
 * L = HabitModel (Network)
 */

interface HabitMapperI<A,V,D,L> {
    fun mapFromHabitEntity(type : A) : V
    fun mapToHabitEntity(type : D ) : A
    fun mapToHabitEntityFromHabitModel(type : L ) : A

    fun mapToHabit(type: A):D

}