package com.example.habit.data.Mapper

import com.example.habit.data.util.HabitRecordSyncType

/**
 * A = HabitEntity
 * V = HabitThumb
 * D = Habit
 * L = HabitModel (Network)
 */

interface HabitMapperI<A,V,D,L> {
    fun mapFromHabitEntity(type : A) : V
    fun mapToHabitEntity(type: D, b: HabitRecordSyncType): A
    fun mapToHabitEntityFromHabitModel(type : L ) : A
    fun mapHabitModelToFromHabitEntity(type : A ) : L

    fun mapToHabit(type: A):D

}