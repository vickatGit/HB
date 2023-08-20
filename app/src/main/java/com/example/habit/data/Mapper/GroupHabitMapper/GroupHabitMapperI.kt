package com.example.habit.data.Mapper.GroupHabitMapper

import com.example.habit.data.util.HabitGroupRecordSyncType

/**
 * A : GroupHabitModel
 * V : GroupHabitEntity
 * D : GroupHabit (domain)
 * L : GroupHabitWithHabits (domain)
 * E : GroupHabitWithHabitsEntity
 */
interface GroupHabitMapperI<A,V,D,L,E> {
    fun toGroupHabitEntity ( type : V ) :  A
    fun toGroupHabitModel ( type : A ) :  V

    fun fromGroupHabit (type : D , syncType :HabitGroupRecordSyncType):V

    fun toGroupHabit(type: V):D
     fun toGroupHabitWithHabits(type: E):L
}