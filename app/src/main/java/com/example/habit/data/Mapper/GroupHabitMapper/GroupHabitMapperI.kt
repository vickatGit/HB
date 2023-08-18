package com.example.habit.data.Mapper.GroupHabitMapper

import com.example.habit.data.util.HabitGroupRecordSyncType

/**
 * A : GroupHabitModel
 * V : GroupHabitEntity
 * D : GroupHabit (domain)
 */
interface GroupHabitMapperI<A,V,D> {
    fun toGroupHabitEntity ( type : V ) :  A
    fun toGroupHabitModel ( type : A ) :  V

    fun fromGroupHabit (type : D , syncType :HabitGroupRecordSyncType):V
}